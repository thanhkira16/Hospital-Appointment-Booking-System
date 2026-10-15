package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private Appointment buildAppointment() {
        User doctorUser = User.builder().fullName("Dr. Smith").email("doctor@test.com").build();
        User patientUser = User.builder().fullName("John Doe").email("patient@test.com").build();
        Doctor doctor = Doctor.builder().id(1L).user(doctorUser).build();
        Patient patient = Patient.builder().id(1L).user(patientUser).build();

        return Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .status(AppointmentStatus.PENDING)
                .reasonForVisit("Checkup")
                .build();
    }

    @Test
    void sendBookingConfirmation_shouldCallMailSender_withCorrectRecipient() {
        Appointment appointment = buildAppointment();

        emailService.sendBookingConfirmation(appointment);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendCancellationNotice_shouldSendToBothPatientAndDoctor() {
        Appointment appointment = buildAppointment();
        appointment.setCancellationReason("Doctor unavailable");

        emailService.sendCancellationNotice(appointment);

        // One email to patient, one to doctor = 2 total calls
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldNotThrow_whenMailSenderFails() {
        Appointment appointment = buildAppointment();
        doThrow(new MailSendException("SMTP connection failed"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // The critical assertion: this must NOT throw, even though
        // mailSender.send() blows up internally
        emailService.sendBookingConfirmation(appointment);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
    @Test
    void confirmation_shouldSnapshotEntityBeforeDispatch_andWaitForCommit() {
        java.util.List<Runnable> queued = new java.util.ArrayList<>();
        emailService.setEmailExecutor(queued::add);
        org.springframework.transaction.support.TransactionSynchronizationManager.initSynchronization();
        org.springframework.transaction.support.TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            Appointment appointment = buildAppointment();
            emailService.sendConfirmationNotice(appointment);
            org.assertj.core.api.Assertions.assertThat(queued).isEmpty();
            verifyNoInteractions(mailSender);
            // A worker must not dereference the entity after the transaction closes.
            appointment.setDoctor(null);
            appointment.setPatient(null);
            org.springframework.transaction.support.TransactionSynchronizationManager
                    .getSynchronizations().forEach(sync -> sync.afterCommit());
            org.assertj.core.api.Assertions.assertThat(queued).hasSize(1);
            queued.getFirst().run();
            var message = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(message.capture());
            org.assertj.core.api.Assertions.assertThat(message.getValue().getTo())
                    .containsExactly("patient@test.com");
            org.assertj.core.api.Assertions.assertThat(message.getValue().getText()).contains("Dr. Smith");
        } finally {
            org.springframework.transaction.support.TransactionSynchronizationManager.clear();
        }
    }

}
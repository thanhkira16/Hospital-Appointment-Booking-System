package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.dto.request.AppointmentRequest;
import com.fpt.hospital_appointment.model.*;
import com.fpt.hospital_appointment.repository.*;
import com.fpt.hospital_appointment.exception.SlotUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookingConcurrencyTest {
    @Autowired AppointmentService service;
    @Autowired UserRepository users;
    @Autowired DoctorRepository doctors;
    @Autowired PatientRepository patients;
    @Autowired DoctorScheduleRepository schedules;
    @Autowired AppointmentRepository appointments;
    @MockitoBean EmailService emailService;

    @Test
    void concurrentRequests_shouldReserveOnlyOneAppointment() throws Exception {
        User doctorUser = users.save(User.builder().fullName("Concurrent Doctor").email("concurrent.doctor@test.com")
                .password("test-password").role(Role.DOCTOR).build());
        User patientUser = users.save(User.builder().fullName("Concurrent Patient").email("concurrent.patient@test.com")
                .password("test-password").role(Role.PATIENT).build());
        Doctor doctor = doctors.save(Doctor.builder().user(doctorUser).specialization("General")
                .qualification("MD").yearsOfExperience(3).phoneNumber("0900000000").consultationFee(100000d).build());
        Patient patient = patients.save(Patient.builder().user(patientUser).dateOfBirth(LocalDate.of(2000,1,1))
                .gender("MALE").phoneNumber("0900000001").address("Test address").build());
        LocalDate date = LocalDate.now().plusDays(14);
        schedules.save(DoctorSchedule.builder().doctor(doctor).dayOfWeek(date.getDayOfWeek())
                .startTime(LocalTime.of(8,0)).endTime(LocalTime.of(10,0)).slotDurationMinutes(30).active(true).build());
        AppointmentRequest request = new AppointmentRequest(doctor.getId(), date, LocalTime.of(8,0), "Checkup");
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> book = () -> {
                start.await();
                try { service.bookAppointment(patient.getId(), request); return true; }
                catch (SlotUnavailableException expected) { return false; }
            };
            Future<Boolean> first = executor.submit(book), second = executor.submit(book);
            start.countDown();
            int successes = (first.get(15, TimeUnit.SECONDS) ? 1 : 0) + (second.get(15, TimeUnit.SECONDS) ? 1 : 0);
            assertThat(successes).isEqualTo(1);
            assertThat(appointments.findByDoctorIdAndAppointmentDate(doctor.getId(), date)).hasSize(1);
        }
    }
}

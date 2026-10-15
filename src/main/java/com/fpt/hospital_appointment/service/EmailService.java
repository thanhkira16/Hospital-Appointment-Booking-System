package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.model.Appointment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

/** Builds notification text inside the caller's transaction and sends it after commit.
 * Only mail messages, never managed JPA entities, cross into the email executor.
 */
@Service
public class EmailService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    public void sendBookingConfirmation(Appointment appointment) {
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorName = appointment.getDoctor().getUser().getFullName();
        String subject = "Appointment Booked - Hospital Appointment System";
        String body = String.format("Dear %s,%n%nYour appointment with Dr. %s has been booked and is PENDING confirmation.%n%n" + "Date: %s%nTime: %s%nReason: %s%n%n" + "You will receive another email once the doctor confirms your appointment.%n%n" + "Thank you for using our Hospital Appointment System.", appointment.getPatient().getUser().getFullName(), doctorName, appointment.getAppointmentDate().format(DATE_FORMAT), appointment.getAppointmentTime().format(TIME_FORMAT), appointment.getReasonForVisit() != null ? appointment.getReasonForVisit() : "Not specified");
        sendEmail(patientEmail, subject, body);
    }

    public void sendConfirmationNotice(Appointment appointment) {
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorName = appointment.getDoctor().getUser().getFullName();
        String subject = "Appointment Confirmed - Hospital Appointment System";
        String body = String.format("Dear %s,%n%nGood news! Dr. %s has CONFIRMED your appointment.%n%n" + "Date: %s%nTime: %s%n%nPlease arrive 10 minutes early.%n%n" + "Thank you for using our Hospital Appointment System.", appointment.getPatient().getUser().getFullName(), doctorName, appointment.getAppointmentDate().format(DATE_FORMAT), appointment.getAppointmentTime().format(TIME_FORMAT));
        sendEmail(patientEmail, subject, body);
    }

    public void sendCancellationNotice(Appointment appointment) {
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorEmail = appointment.getDoctor().getUser().getEmail();
        String doctorName = appointment.getDoctor().getUser().getFullName();
        String patientName = appointment.getPatient().getUser().getFullName();
        String subject = "Appointment Cancelled - Hospital Appointment System";
        String patientBody = String.format("Dear %s,%n%nYour appointment with Dr. %s on %s at %s has been CANCELLED.%n%n" + "Reason: %s%n%nPlease book a new appointment if needed.%n%n" + "Thank you for using our Hospital Appointment System.", patientName, doctorName, appointment.getAppointmentDate().format(DATE_FORMAT), appointment.getAppointmentTime().format(TIME_FORMAT), appointment.getCancellationReason() != null ? appointment.getCancellationReason() : "Not specified");
        String doctorBody = String.format("Dear Dr. %s,%n%nThe appointment with %s on %s at %s has been CANCELLED.%n%n" + "Reason: %s%n%nThis slot is now available for other bookings.", doctorName, patientName, appointment.getAppointmentDate().format(DATE_FORMAT), appointment.getAppointmentTime().format(TIME_FORMAT), appointment.getCancellationReason() != null ? appointment.getCancellationReason() : "Not specified");
        sendEmail(patientEmail, subject, patientBody);
        sendEmail(doctorEmail, subject, doctorBody);
    }

    public void sendCompletionNotice(Appointment appointment) {
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorName = appointment.getDoctor().getUser().getFullName();
        String subject = "Appointment Completed - Hospital Appointment System";
        String body = String.format("Dear %s,%n%nYour appointment with Dr. %s on %s has been marked as COMPLETED.%n%n" + "If a medical record was created, you can view it in your patient dashboard.%n%n" + "Thank you for using our Hospital Appointment System.", appointment.getPatient().getUser().getFullName(), doctorName, appointment.getAppointmentDate().format(DATE_FORMAT));
        sendEmail(patientEmail, subject, body);
    }

    public void sendPasswordResetEmail(String toEmail, String fullName, String resetLink) {
        String subject = "Password Reset Request - Hospital Appointment System";
        String body = String.format("Dear %s,%n%nWe received a request to reset your password.%n%n" + "Click the link below to choose a new password. This link expires in 30 minutes:%n%s%n%n" + "If you did not request a password reset, you can safely ignore this email.%n%n" + "Thank you for using our Hospital Appointment System.", fullName, resetLink);
        sendEmail(toEmail, subject, body);
    }

    private TaskExecutor emailExecutor = Runnable::run;

    @Autowired
    public void setEmailExecutor(@Qualifier("emailTaskExecutor") TaskExecutor emailExecutor) {
        this.emailExecutor = emailExecutor;
    }

    // ---------- Private helper ----------
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        Runnable dispatch = () -> {
            try {
                emailExecutor.execute(() -> {
                    try {
                        mailSender.send(message);
                        log.info("Email sent successfully to {}", to);
                    } catch (Exception ex) {
                        log.error("Failed to send email to {}: {}", to, ex.getMessage());
                    }
                });
            } catch (Exception ex) {
                log.error("Failed to queue email to {}: {}", to, ex.getMessage());
            }
        };
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    dispatch.run();
                }
            });
        } else {
            dispatch.run();
        }
    }

    public EmailService(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}

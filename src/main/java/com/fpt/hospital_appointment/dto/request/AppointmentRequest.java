package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDate appointmentDate;
    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reasonForVisit;

    public Long getDoctorId() {
        return this.doctorId;
    }

    public LocalDate getAppointmentDate() {
        return this.appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return this.appointmentTime;
    }

    public String getReasonForVisit() {
        return this.reasonForVisit;
    }

    public void setDoctorId(final Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setAppointmentDate(final LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(final LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setReasonForVisit(final String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public AppointmentRequest() {
    }

    public AppointmentRequest(final Long doctorId, final LocalDate appointmentDate, final LocalTime appointmentTime, final String reasonForVisit) {
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reasonForVisit = reasonForVisit;
    }
}

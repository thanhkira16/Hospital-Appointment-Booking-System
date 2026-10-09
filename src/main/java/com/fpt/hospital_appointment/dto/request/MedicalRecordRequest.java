package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MedicalRecordRequest {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;
    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;
    private String prescription;
    private String doctorNotes;

    public Long getAppointmentId() {
        return this.appointmentId;
    }

    public String getDiagnosis() {
        return this.diagnosis;
    }

    public String getPrescription() {
        return this.prescription;
    }

    public String getDoctorNotes() {
        return this.doctorNotes;
    }

    public void setAppointmentId(final Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setDiagnosis(final String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setPrescription(final String prescription) {
        this.prescription = prescription;
    }

    public void setDoctorNotes(final String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public MedicalRecordRequest() {
    }

    public MedicalRecordRequest(final Long appointmentId, final String diagnosis, final String prescription, final String doctorNotes) {
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.doctorNotes = doctorNotes;
    }
}

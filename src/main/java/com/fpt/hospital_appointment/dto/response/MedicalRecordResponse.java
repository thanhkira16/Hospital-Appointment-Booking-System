package com.fpt.hospital_appointment.dto.response;

import java.time.LocalDateTime;

public class MedicalRecordResponse {
    private Long id;
    private Long appointmentId;
    private String diagnosis;
    private String prescription;
    private String doctorNotes;
    private LocalDateTime createdAt;


    public static class MedicalRecordResponseBuilder {
        private Long id;
        private Long appointmentId;
        private String diagnosis;
        private String prescription;
        private String doctorNotes;
        private LocalDateTime createdAt;

        MedicalRecordResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecordResponse.MedicalRecordResponseBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecordResponse.MedicalRecordResponseBuilder appointmentId(final Long appointmentId) {
            this.appointmentId = appointmentId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecordResponse.MedicalRecordResponseBuilder diagnosis(final String diagnosis) {
            this.diagnosis = diagnosis;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecordResponse.MedicalRecordResponseBuilder prescription(final String prescription) {
            this.prescription = prescription;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecordResponse.MedicalRecordResponseBuilder doctorNotes(final String doctorNotes) {
            this.doctorNotes = doctorNotes;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecordResponse.MedicalRecordResponseBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MedicalRecordResponse build() {
            return new MedicalRecordResponse(this.id, this.appointmentId, this.diagnosis, this.prescription, this.doctorNotes, this.createdAt);
        }

        @Override
        public String toString() {
            return "MedicalRecordResponse.MedicalRecordResponseBuilder(id=" + this.id + ", appointmentId=" + this.appointmentId + ", diagnosis=" + this.diagnosis + ", prescription=" + this.prescription + ", doctorNotes=" + this.doctorNotes + ", createdAt=" + this.createdAt + ")";
        }
    }

    public static MedicalRecordResponse.MedicalRecordResponseBuilder builder() {
        return new MedicalRecordResponse.MedicalRecordResponseBuilder();
    }

    public Long getId() {
        return this.id;
    }

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

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MedicalRecordResponse() {
    }

    public MedicalRecordResponse(final Long id, final Long appointmentId, final String diagnosis, final String prescription, final String doctorNotes, final LocalDateTime createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.doctorNotes = doctorNotes;
        this.createdAt = createdAt;
    }
}

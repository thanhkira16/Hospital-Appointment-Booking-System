package com.fpt.hospital_appointment.model;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;
    @Column(length = 1000, nullable = false)
    private String diagnosis;
    @Column(length = 1000)
    private String prescription;
    @Column(length = 1000)
    private String doctorNotes;
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    public static class MedicalRecordBuilder {
        private Long id;
        private Appointment appointment;
        private String diagnosis;
        private String prescription;
        private String doctorNotes;
        private LocalDateTime createdAt;

        MedicalRecordBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecord.MedicalRecordBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecord.MedicalRecordBuilder appointment(final Appointment appointment) {
            this.appointment = appointment;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecord.MedicalRecordBuilder diagnosis(final String diagnosis) {
            this.diagnosis = diagnosis;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecord.MedicalRecordBuilder prescription(final String prescription) {
            this.prescription = prescription;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecord.MedicalRecordBuilder doctorNotes(final String doctorNotes) {
            this.doctorNotes = doctorNotes;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public MedicalRecord.MedicalRecordBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MedicalRecord build() {
            return new MedicalRecord(this.id, this.appointment, this.diagnosis, this.prescription, this.doctorNotes, this.createdAt);
        }

        @Override
        public String toString() {
            return "MedicalRecord.MedicalRecordBuilder(id=" + this.id + ", appointment=" + this.appointment + ", diagnosis=" + this.diagnosis + ", prescription=" + this.prescription + ", doctorNotes=" + this.doctorNotes + ", createdAt=" + this.createdAt + ")";
        }
    }

    public static MedicalRecord.MedicalRecordBuilder builder() {
        return new MedicalRecord.MedicalRecordBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public Appointment getAppointment() {
        return this.appointment;
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

    public void setAppointment(final Appointment appointment) {
        this.appointment = appointment;
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

    public MedicalRecord() {
    }

    public MedicalRecord(final Long id, final Appointment appointment, final String diagnosis, final String prescription, final String doctorNotes, final LocalDateTime createdAt) {
        this.id = id;
        this.appointment = appointment;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.doctorNotes = doctorNotes;
        this.createdAt = createdAt;
    }
}

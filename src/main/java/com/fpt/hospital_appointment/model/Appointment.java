package com.fpt.hospital_appointment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    @Column(nullable = false)
    private LocalDate appointmentDate;
    @Column(nullable = false)
    private LocalTime appointmentTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
    @Column(length = 500)
    private String reasonForVisit;
    @Column(length = 500)
    private String cancellationReason;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalRecord medicalRecord;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = AppointmentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public static class AppointmentBuilder {
        private Long id;
        private Doctor doctor;
        private Patient patient;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private AppointmentStatus status;
        private String reasonForVisit;
        private String cancellationReason;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private MedicalRecord medicalRecord;

        AppointmentBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder doctor(final Doctor doctor) {
            this.doctor = doctor;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder patient(final Patient patient) {
            this.patient = patient;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder appointmentDate(final LocalDate appointmentDate) {
            this.appointmentDate = appointmentDate;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder appointmentTime(final LocalTime appointmentTime) {
            this.appointmentTime = appointmentTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder status(final AppointmentStatus status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder reasonForVisit(final String reasonForVisit) {
            this.reasonForVisit = reasonForVisit;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder cancellationReason(final String cancellationReason) {
            this.cancellationReason = cancellationReason;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Appointment.AppointmentBuilder medicalRecord(final MedicalRecord medicalRecord) {
            this.medicalRecord = medicalRecord;
            return this;
        }

        public Appointment build() {
            return new Appointment(this.id, this.doctor, this.patient, this.appointmentDate, this.appointmentTime, this.status, this.reasonForVisit, this.cancellationReason, this.createdAt, this.updatedAt, this.medicalRecord);
        }

        @Override
        public String toString() {
            return "Appointment.AppointmentBuilder(id=" + this.id + ", doctor=" + this.doctor + ", patient=" + this.patient + ", appointmentDate=" + this.appointmentDate + ", appointmentTime=" + this.appointmentTime + ", status=" + this.status + ", reasonForVisit=" + this.reasonForVisit + ", cancellationReason=" + this.cancellationReason + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", medicalRecord=" + this.medicalRecord + ")";
        }
    }

    public static Appointment.AppointmentBuilder builder() {
        return new Appointment.AppointmentBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public LocalDate getAppointmentDate() {
        return this.appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return this.appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return this.status;
    }

    public String getReasonForVisit() {
        return this.reasonForVisit;
    }

    public String getCancellationReason() {
        return this.cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public MedicalRecord getMedicalRecord() {
        return this.medicalRecord;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setDoctor(final Doctor doctor) {
        this.doctor = doctor;
    }

    public void setPatient(final Patient patient) {
        this.patient = patient;
    }

    public void setAppointmentDate(final LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(final LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setStatus(final AppointmentStatus status) {
        this.status = status;
    }

    public void setReasonForVisit(final String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public void setCancellationReason(final String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setMedicalRecord(final MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public Appointment() {
    }

    public Appointment(final Long id, final Doctor doctor, final Patient patient, final LocalDate appointmentDate, final LocalTime appointmentTime, final AppointmentStatus status, final String reasonForVisit, final String cancellationReason, final LocalDateTime createdAt, final LocalDateTime updatedAt, final MedicalRecord medicalRecord) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reasonForVisit = reasonForVisit;
        this.cancellationReason = cancellationReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.medicalRecord = medicalRecord;
    }
}

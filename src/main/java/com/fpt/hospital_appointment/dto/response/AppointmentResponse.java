package com.fpt.hospital_appointment.dto.response;

import com.fpt.hospital_appointment.model.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private Long patientId;
    private String patientName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private String reasonForVisit;
    private String cancellationReason;


    public static class AppointmentResponseBuilder {
        private Long id;
        private Long doctorId;
        private String doctorName;
        private String doctorSpecialization;
        private Long patientId;
        private String patientName;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private AppointmentStatus status;
        private String reasonForVisit;
        private String cancellationReason;

        AppointmentResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder doctorId(final Long doctorId) {
            this.doctorId = doctorId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder doctorName(final String doctorName) {
            this.doctorName = doctorName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder doctorSpecialization(final String doctorSpecialization) {
            this.doctorSpecialization = doctorSpecialization;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder patientId(final Long patientId) {
            this.patientId = patientId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder patientName(final String patientName) {
            this.patientName = patientName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder appointmentDate(final LocalDate appointmentDate) {
            this.appointmentDate = appointmentDate;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder appointmentTime(final LocalTime appointmentTime) {
            this.appointmentTime = appointmentTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder status(final AppointmentStatus status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder reasonForVisit(final String reasonForVisit) {
            this.reasonForVisit = reasonForVisit;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AppointmentResponse.AppointmentResponseBuilder cancellationReason(final String cancellationReason) {
            this.cancellationReason = cancellationReason;
            return this;
        }

        public AppointmentResponse build() {
            return new AppointmentResponse(this.id, this.doctorId, this.doctorName, this.doctorSpecialization, this.patientId, this.patientName, this.appointmentDate, this.appointmentTime, this.status, this.reasonForVisit, this.cancellationReason);
        }

        @Override
        public String toString() {
            return "AppointmentResponse.AppointmentResponseBuilder(id=" + this.id + ", doctorId=" + this.doctorId + ", doctorName=" + this.doctorName + ", doctorSpecialization=" + this.doctorSpecialization + ", patientId=" + this.patientId + ", patientName=" + this.patientName + ", appointmentDate=" + this.appointmentDate + ", appointmentTime=" + this.appointmentTime + ", status=" + this.status + ", reasonForVisit=" + this.reasonForVisit + ", cancellationReason=" + this.cancellationReason + ")";
        }
    }

    public static AppointmentResponse.AppointmentResponseBuilder builder() {
        return new AppointmentResponse.AppointmentResponseBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

    public String getDoctorName() {
        return this.doctorName;
    }

    public String getDoctorSpecialization() {
        return this.doctorSpecialization;
    }

    public Long getPatientId() {
        return this.patientId;
    }

    public String getPatientName() {
        return this.patientName;
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

    public void setId(final Long id) {
        this.id = id;
    }

    public void setDoctorId(final Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(final String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDoctorSpecialization(final String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public void setPatientId(final Long patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(final String patientName) {
        this.patientName = patientName;
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

    public AppointmentResponse() {
    }

    public AppointmentResponse(final Long id, final Long doctorId, final String doctorName, final String doctorSpecialization, final Long patientId, final String patientName, final LocalDate appointmentDate, final LocalTime appointmentTime, final AppointmentStatus status, final String reasonForVisit, final String cancellationReason) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.patientId = patientId;
        this.patientName = patientName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reasonForVisit = reasonForVisit;
        this.cancellationReason = cancellationReason;
    }
}

package com.fpt.hospital_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @NotBlank(message = "Specoalization is required")
    @Column(nullable = false)
    private String specialization;
    @NotBlank(message = "Qualification is required")
    @Column(nullable = false)
    private String qualification;
    @Column(nullable = false)
    private Integer yearsOfExperience;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private Double consultationFee;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorSchedule> schedules;
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;

    private static List<DoctorSchedule> $default$schedules() {
        return new ArrayList<>();
    }

    private static List<Appointment> $default$appointments() {
        return new ArrayList<>();
    }


    public static class DoctorBuilder {
        private Long id;
        private User user;
        private String specialization;
        private String qualification;
        private Integer yearsOfExperience;
        private String phoneNumber;
        private Double consultationFee;
        private boolean schedules$set;
        private List<DoctorSchedule> schedules$value;
        private boolean appointments$set;
        private List<Appointment> appointments$value;

        DoctorBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder user(final User user) {
            this.user = user;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder specialization(final String specialization) {
            this.specialization = specialization;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder qualification(final String qualification) {
            this.qualification = qualification;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder yearsOfExperience(final Integer yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder consultationFee(final Double consultationFee) {
            this.consultationFee = consultationFee;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder schedules(final List<DoctorSchedule> schedules) {
            this.schedules$value = schedules;
            schedules$set = true;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Doctor.DoctorBuilder appointments(final List<Appointment> appointments) {
            this.appointments$value = appointments;
            appointments$set = true;
            return this;
        }

        public Doctor build() {
            List<DoctorSchedule> schedules$value = this.schedules$value;
            if (!this.schedules$set) schedules$value = Doctor.$default$schedules();
            List<Appointment> appointments$value = this.appointments$value;
            if (!this.appointments$set) appointments$value = Doctor.$default$appointments();
            return new Doctor(this.id, this.user, this.specialization, this.qualification, this.yearsOfExperience, this.phoneNumber, this.consultationFee, schedules$value, appointments$value);
        }

        @Override
        public String toString() {
            return "Doctor.DoctorBuilder(id=" + this.id + ", user=" + this.user + ", specialization=" + this.specialization + ", qualification=" + this.qualification + ", yearsOfExperience=" + this.yearsOfExperience + ", phoneNumber=" + this.phoneNumber + ", consultationFee=" + this.consultationFee + ", schedules$value=" + this.schedules$value + ", appointments$value=" + this.appointments$value + ")";
        }
    }

    public static Doctor.DoctorBuilder builder() {
        return new Doctor.DoctorBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public String getSpecialization() {
        return this.specialization;
    }

    public String getQualification() {
        return this.qualification;
    }

    public Integer getYearsOfExperience() {
        return this.yearsOfExperience;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Double getConsultationFee() {
        return this.consultationFee;
    }

    public List<DoctorSchedule> getSchedules() {
        return this.schedules;
    }

    public List<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public void setSpecialization(final String specialization) {
        this.specialization = specialization;
    }

    public void setQualification(final String qualification) {
        this.qualification = qualification;
    }

    public void setYearsOfExperience(final Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setConsultationFee(final Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public void setSchedules(final List<DoctorSchedule> schedules) {
        this.schedules = schedules;
    }

    public void setAppointments(final List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Doctor() {
        this.schedules = Doctor.$default$schedules();
        this.appointments = Doctor.$default$appointments();
    }

    public Doctor(final Long id, final User user, final String specialization, final String qualification, final Integer yearsOfExperience, final String phoneNumber, final Double consultationFee, final List<DoctorSchedule> schedules, final List<Appointment> appointments) {
        this.id = id;
        this.user = user;
        this.specialization = specialization;
        this.qualification = qualification;
        this.yearsOfExperience = yearsOfExperience;
        this.phoneNumber = phoneNumber;
        this.consultationFee = consultationFee;
        this.schedules = schedules;
        this.appointments = appointments;
    }
}

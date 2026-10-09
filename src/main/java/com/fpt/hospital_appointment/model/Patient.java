package com.fpt.hospital_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @NotBlank(message = "Gender is required")
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String address;
    @Column(length = 1000)
    private String medicalHistory;
    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments;

    private static List<Appointment> $default$appointments() {
        return new ArrayList<>();
    }


    public static class PatientBuilder {
        private Long id;
        private User user;
        private LocalDate dateOfBirth;
        private String gender;
        private String phoneNumber;
        private String address;
        private String medicalHistory;
        private boolean appointments$set;
        private List<Appointment> appointments$value;

        PatientBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder user(final User user) {
            this.user = user;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder dateOfBirth(final LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder gender(final String gender) {
            this.gender = gender;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder address(final String address) {
            this.address = address;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder medicalHistory(final String medicalHistory) {
            this.medicalHistory = medicalHistory;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public Patient.PatientBuilder appointments(final List<Appointment> appointments) {
            this.appointments$value = appointments;
            appointments$set = true;
            return this;
        }

        public Patient build() {
            List<Appointment> appointments$value = this.appointments$value;
            if (!this.appointments$set) appointments$value = Patient.$default$appointments();
            return new Patient(this.id, this.user, this.dateOfBirth, this.gender, this.phoneNumber, this.address, this.medicalHistory, appointments$value);
        }

        @Override
        public String toString() {
            return "Patient.PatientBuilder(id=" + this.id + ", user=" + this.user + ", dateOfBirth=" + this.dateOfBirth + ", gender=" + this.gender + ", phoneNumber=" + this.phoneNumber + ", address=" + this.address + ", medicalHistory=" + this.medicalHistory + ", appointments$value=" + this.appointments$value + ")";
        }
    }

    public static Patient.PatientBuilder builder() {
        return new Patient.PatientBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public String getMedicalHistory() {
        return this.medicalHistory;
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

    public void setDateOfBirth(final LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public void setMedicalHistory(final String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void setAppointments(final List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Patient() {
        this.appointments = Patient.$default$appointments();
    }

    public Patient(final Long id, final User user, final LocalDate dateOfBirth, final String gender, final String phoneNumber, final String address, final String medicalHistory, final List<Appointment> appointments) {
        this.id = id;
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.appointments = appointments;
    }
}

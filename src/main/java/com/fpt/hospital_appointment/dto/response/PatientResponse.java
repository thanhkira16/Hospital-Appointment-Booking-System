package com.fpt.hospital_appointment.dto.response;

import java.time.LocalDate;

public class PatientResponse {
    private Long id;
    private String fullName; // pulled from linked User
    private String email; // pulled from linked User
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String address;
    private String medicalHistory;


    public static class PatientResponseBuilder {
        private Long id;
        private String fullName;
        private String email;
        private LocalDate dateOfBirth;
        private String gender;
        private String phoneNumber;
        private String address;
        private String medicalHistory;

        PatientResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder fullName(final String fullName) {
            this.fullName = fullName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder email(final String email) {
            this.email = email;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder dateOfBirth(final LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder gender(final String gender) {
            this.gender = gender;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder address(final String address) {
            this.address = address;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public PatientResponse.PatientResponseBuilder medicalHistory(final String medicalHistory) {
            this.medicalHistory = medicalHistory;
            return this;
        }

        public PatientResponse build() {
            return new PatientResponse(this.id, this.fullName, this.email, this.dateOfBirth, this.gender, this.phoneNumber, this.address, this.medicalHistory);
        }

        @Override
        public String toString() {
            return "PatientResponse.PatientResponseBuilder(id=" + this.id + ", fullName=" + this.fullName + ", email=" + this.email + ", dateOfBirth=" + this.dateOfBirth + ", gender=" + this.gender + ", phoneNumber=" + this.phoneNumber + ", address=" + this.address + ", medicalHistory=" + this.medicalHistory + ")";
        }
    }

    public static PatientResponse.PatientResponseBuilder builder() {
        return new PatientResponse.PatientResponseBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
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

    public void setId(final Long id) {
        this.id = id;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(final String email) {
        this.email = email;
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

    public PatientResponse() {
    }

    public PatientResponse(final Long id, final String fullName, final String email, final LocalDate dateOfBirth, final String gender, final String phoneNumber, final String address, final String medicalHistory) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }
}

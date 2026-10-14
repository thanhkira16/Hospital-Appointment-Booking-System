package com.fpt.hospital_appointment.dto.response;

public class DoctorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String specialization;
    private String qualification;
    private Integer yearsOfExperience;
    private String phoneNumber;
    private Double consultationFee;


    public static class DoctorResponseBuilder {
        private Long id;
        private String fullName;
        private String email;
        private String specialization;
        private String qualification;
        private Integer yearsOfExperience;
        private String phoneNumber;
        private Double consultationFee;

        DoctorResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder fullName(final String fullName) {
            this.fullName = fullName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder email(final String email) {
            this.email = email;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder specialization(final String specialization) {
            this.specialization = specialization;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder qualification(final String qualification) {
            this.qualification = qualification;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder yearsOfExperience(final Integer yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorResponse.DoctorResponseBuilder consultationFee(final Double consultationFee) {
            this.consultationFee = consultationFee;
            return this;
        }

        public DoctorResponse build() {
            return new DoctorResponse(this.id, this.fullName, this.email, this.specialization, this.qualification, this.yearsOfExperience, this.phoneNumber, this.consultationFee);
        }

        @Override
        public String toString() {
            return "DoctorResponse.DoctorResponseBuilder(id=" + this.id + ", fullName=" + this.fullName + ", email=" + this.email + ", specialization=" + this.specialization + ", qualification=" + this.qualification + ", yearsOfExperience=" + this.yearsOfExperience + ", phoneNumber=" + this.phoneNumber + ", consultationFee=" + this.consultationFee + ")";
        }
    }

    public static DoctorResponse.DoctorResponseBuilder builder() {
        return new DoctorResponse.DoctorResponseBuilder();
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

    public void setId(final Long id) {
        this.id = id;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(final String email) {
        this.email = email;
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

    public DoctorResponse() {
    }

    public DoctorResponse(final Long id, final String fullName, final String email, final String specialization, final String qualification, final Integer yearsOfExperience, final String phoneNumber, final Double consultationFee) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.specialization = specialization;
        this.qualification = qualification;
        this.yearsOfExperience = yearsOfExperience;
        this.phoneNumber = phoneNumber;
        this.consultationFee = consultationFee;
    }
}

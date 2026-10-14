package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DoctorProfileRequest {
    @NotBlank(message = "Specialization is required")
    private String specialization;
    @NotBlank(message = "Qualification is required")
    private String qualification;
    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotNull(message = "Consultation fee is required")
    @Positive(message = "Consultation fee must be positive")
    private Double consultationFee;

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

    public DoctorProfileRequest() {
    }

    public DoctorProfileRequest(final String specialization, final String qualification, final Integer yearsOfExperience, final String phoneNumber, final Double consultationFee) {
        this.specialization = specialization;
        this.qualification = qualification;
        this.yearsOfExperience = yearsOfExperience;
        this.phoneNumber = phoneNumber;
        this.consultationFee = consultationFee;
    }
}

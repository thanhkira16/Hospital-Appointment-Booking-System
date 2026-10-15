package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AdminCreateDoctorRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
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
    // Whether the doctor must change this admin-assigned password on next login.
    private boolean requirePasswordChange = true;

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
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

    public boolean isRequirePasswordChange() {
        return this.requirePasswordChange;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setPassword(final String password) {
        this.password = password;
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

    public void setRequirePasswordChange(final boolean requirePasswordChange) {
        this.requirePasswordChange = requirePasswordChange;
    }
}

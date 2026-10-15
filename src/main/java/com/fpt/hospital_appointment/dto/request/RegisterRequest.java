package com.fpt.hospital_appointment.dto.request;

import com.fpt.hospital_appointment.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NotNull(message = "Role is required")
    private Role role;
    private String specialization;
    private String qualification;
    @jakarta.validation.constraints.Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;
    @jakarta.validation.constraints.Positive(message = "Consultation fee must be positive")
    private Double consultationFee;
    private String dateOfBirth;
    private String gender;
    private String address;
    private String medicalHistory;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public Role getRole() {
        return this.role;
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

    public Double getConsultationFee() {
        return this.consultationFee;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public String getAddress() {
        return this.address;
    }

    public String getMedicalHistory() {
        return this.medicalHistory;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
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

    public void setRole(final Role role) {
        this.role = role;
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

    public void setConsultationFee(final Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public void setDateOfBirth(final String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public void setMedicalHistory(final String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public RegisterRequest() {
    }

    public RegisterRequest(final String fullName, final String email, final String password, final Role role, final String specialization, final String qualification, final Integer yearsOfExperience, final Double consultationFee, final String dateOfBirth, final String gender, final String address, final String medicalHistory, final String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.specialization = specialization;
        this.qualification = qualification;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.phoneNumber = phoneNumber;
    }
}

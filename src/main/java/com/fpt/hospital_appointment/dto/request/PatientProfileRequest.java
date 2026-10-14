package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public class PatientProfileRequest {
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    @NotBlank(message = "Gender is required")
    private String gender;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    private String address;
    private String medicalHistory;

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

    public PatientProfileRequest() {
    }

    public PatientProfileRequest(final LocalDate dateOfBirth, final String gender, final String phoneNumber, final String address, final String medicalHistory) {
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }
}

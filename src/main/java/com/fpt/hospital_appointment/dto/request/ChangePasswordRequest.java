package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    public String getCurrentPassword() {
        return this.currentPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setCurrentPassword(final String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }
}

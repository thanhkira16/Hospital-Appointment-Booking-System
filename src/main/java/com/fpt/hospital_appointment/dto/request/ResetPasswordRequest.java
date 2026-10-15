package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    private String token;
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    public String getToken() {
        return this.token;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }
}

package com.fpt.hospital_appointment.controller;

import com.fpt.hospital_appointment.dto.ApiResponse;
import com.fpt.hospital_appointment.dto.request.ChangePasswordRequest;
import com.fpt.hospital_appointment.dto.request.ForgotPasswordRequest;
import com.fpt.hospital_appointment.dto.request.LoginRequest;
import com.fpt.hospital_appointment.dto.request.RegisterRequest;
import com.fpt.hospital_appointment.dto.request.ResetPasswordRequest;
import com.fpt.hospital_appointment.dto.response.AuthResponse;
import com.fpt.hospital_appointment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Login successful", response));
    }

    // Lets a logged-in user (e.g. a doctor given a temporary password by an
    // admin) set their own password and clear the forced-change flag.
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    // Always returns success, whether or not the email is registered, so
    // callers can't use this to discover which emails have accounts.
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("If that email is registered, a password reset link has been sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }
}

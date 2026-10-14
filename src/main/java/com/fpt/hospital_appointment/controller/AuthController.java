package com.fpt.hospital_appointment.controller;

import com.fpt.hospital_appointment.dto.ApiResponse;
import com.fpt.hospital_appointment.dto.request.LoginRequest;
import com.fpt.hospital_appointment.dto.request.RegisterRequest;
import com.fpt.hospital_appointment.dto.response.AuthResponse;
import com.fpt.hospital_appointment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }
}

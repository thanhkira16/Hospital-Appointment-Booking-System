package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.dto.request.ForgotPasswordRequest;
import com.fpt.hospital_appointment.dto.request.LoginRequest;
import com.fpt.hospital_appointment.dto.request.RegisterRequest;
import com.fpt.hospital_appointment.dto.request.ResetPasswordRequest;
import com.fpt.hospital_appointment.dto.response.AuthResponse;
import com.fpt.hospital_appointment.exception.DuplicateResourceException;
import com.fpt.hospital_appointment.exception.InvalidRequestException;
import com.fpt.hospital_appointment.model.Doctor;
import com.fpt.hospital_appointment.model.Patient;
import com.fpt.hospital_appointment.model.PasswordResetToken;
import com.fpt.hospital_appointment.model.Role;
import com.fpt.hospital_appointment.model.User;
import com.fpt.hospital_appointment.repository.DoctorRepository;
import com.fpt.hospital_appointment.repository.PasswordResetTokenRepository;
import com.fpt.hospital_appointment.repository.PatientRepository;
import com.fpt.hospital_appointment.repository.UserRepository;
import com.fpt.hospital_appointment.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
public class AuthService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);
    private static final int RESET_TOKEN_VALIDITY_MINUTES = 30;

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        request.setEmail(request.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        if (request.getRole() == Role.ADMIN) {
            throw new InvalidRequestException("Public registration is not available for ADMIN");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }
        User user = User.builder().fullName(request.getFullName()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).enabled(true).build();
        user = userRepository.save(user);
        if (request.getRole() == Role.DOCTOR) {
            createDoctorProfile(request, user);
        } else if (request.getRole() == Role.PATIENT) {
            createPatientProfile(request, user);
        }
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        request.setEmail(request.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
        String token = jwtUtil.generateToken(userDetails);
        return AuthResponse.builder().userId(user.getId()).fullName(user.getFullName()).email(user.getEmail()).role(user.getRole()).token(token).tokenType("Bearer").mustChangePassword(user.isMustChangePassword()).build();
    }

    @Transactional
    public void changePassword(String currentUserEmail, com.fpt.hospital_appointment.dto.request.ChangePasswordRequest request) {
        User user = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }

    // Always succeeds from the caller's point of view, whether or not the
    // email exists, so this endpoint can't be used to enumerate accounts.
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusMinutes(RESET_TOKEN_VALIDITY_MINUTES));
            passwordResetTokenRepository.save(resetToken);
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            try {
                emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink);
            } catch (Exception ex) {
                log.error("Failed to send password reset email to {}: {}", user.getEmail(), ex.getMessage());
            }
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken()).orElseThrow(() -> new InvalidRequestException("Invalid or expired password reset token"));
        if (resetToken.isUsed()) {
            throw new InvalidRequestException("This password reset link has already been used");
        }
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("This password reset link has expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // ---------- Private helpers ----------
    private void createDoctorProfile(RegisterRequest request, User user) {
        if (request.getSpecialization() == null || request.getSpecialization().isBlank() || request.getQualification() == null || request.getQualification().isBlank() || request.getYearsOfExperience() == null || request.getConsultationFee() == null) {
            throw new InvalidRequestException("Specialization, qualification, yearsOfExperience, and consultationFee are required for DOCTOR registration");
        }
        Doctor doctor = Doctor.builder().user(user).specialization(request.getSpecialization()).qualification(request.getQualification()).yearsOfExperience(request.getYearsOfExperience()).phoneNumber(request.getPhoneNumber()).consultationFee(request.getConsultationFee()).build();
        doctorRepository.save(doctor);
    }

    private void createPatientProfile(RegisterRequest request, User user) {
        if (request.getDateOfBirth() == null || request.getDateOfBirth().isBlank() || request.getGender() == null || request.getGender().isBlank() || request.getAddress() == null || request.getAddress().isBlank()) {
            throw new InvalidRequestException("dateOfBirth, gender, and address are required for PATIENT registration");
        }
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(request.getDateOfBirth());
        } catch (DateTimeParseException ex) {
            throw new InvalidRequestException("dateOfBirth must be in YYYY-MM-DD format");
        }
        if (!dateOfBirth.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Date of birth must be in the past");
        }
        Patient patient = Patient.builder().user(user).dateOfBirth(dateOfBirth).gender(request.getGender()).phoneNumber(request.getPhoneNumber()).address(request.getAddress()).medicalHistory(request.getMedicalHistory()).build();
        patientRepository.save(patient);
    }

    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder().username(user.getEmail()).password(user.getPassword()).authorities("ROLE_" + user.getRole().name()).build();
        String token = jwtUtil.generateToken(userDetails);
        return AuthResponse.builder().userId(user.getId()).fullName(user.getFullName()).email(user.getEmail()).role(user.getRole()).token(token).tokenType("Bearer").mustChangePassword(user.isMustChangePassword()).build();
    }

    public AuthService(final UserRepository userRepository, final DoctorRepository doctorRepository, final PatientRepository patientRepository, final PasswordResetTokenRepository passwordResetTokenRepository, final PasswordEncoder passwordEncoder, final JwtUtil jwtUtil, final AuthenticationManager authenticationManager, final EmailService emailService) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }
}

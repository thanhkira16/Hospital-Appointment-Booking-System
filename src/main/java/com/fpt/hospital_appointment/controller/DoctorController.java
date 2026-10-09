package com.fpt.hospital_appointment.controller;

import com.fpt.hospital_appointment.dto.ApiResponse;
import com.fpt.hospital_appointment.dto.request.DoctorProfileRequest;
import com.fpt.hospital_appointment.dto.response.DoctorResponse;
import com.fpt.hospital_appointment.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fpt.hospital_appointment.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DoctorResponse>>> getAllDoctors(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        var doctors = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(ApiResponse.success("Doctors fetched successfully", doctors));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole(\'DOCTOR\')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getCurrentDoctor(Authentication authentication) {
        DoctorResponse doctor = doctorService.getDoctorByEmail(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Current doctor profile fetched successfully", doctor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor fetched successfully", doctor));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(\'ADMIN\') or @doctorService.isOwner(#id, authentication.name)")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctorProfile(@PathVariable Long id, @Valid @RequestBody DoctorProfileRequest request) {
        DoctorResponse updated = doctorService.updateDoctorProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Doctor profile updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(\'ADMIN\')")
    public ResponseEntity<ApiResponse<Object>> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Doctor deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<DoctorResponse>>> searchDoctors(@RequestParam(required = false) String specialization, @RequestParam(required = false) Integer minExperience, @RequestParam(required = false) Double maxFee, @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        PageResponse<DoctorResponse> results = doctorService.searchDoctors(specialization, minExperience, maxFee, pageable);
        return ResponseEntity.ok(ApiResponse.success("Doctor search results", results));
    }

    public DoctorController(final DoctorService doctorService) {
        this.doctorService = doctorService;
    }
}

package com.fpt.hospital_appointment.controller;

import com.fpt.hospital_appointment.dto.ApiResponse;
import com.fpt.hospital_appointment.dto.PageResponse;
import com.fpt.hospital_appointment.dto.request.PatientProfileRequest;
import com.fpt.hospital_appointment.dto.response.PatientResponse;
import com.fpt.hospital_appointment.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasRole(\'ADMIN\') or hasRole(\'DOCTOR\')")
    public ResponseEntity<ApiResponse<PageResponse<PatientResponse>>> getAllPatients(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        var patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(ApiResponse.success("Patients fetched successfully", patients));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole(\'PATIENT\')")
    public ResponseEntity<ApiResponse<PatientResponse>> getCurrentPatient(Authentication authentication) {
        PatientResponse patient = patientService.getPatientByEmail(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Current patient profile fetched successfully", patient));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(\'ADMIN\') or hasRole(\'DOCTOR\') or @patientService.isOwner(#id, authentication.name)")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable Long id) {
        PatientResponse patient = patientService.getPatientById(id);
        return ResponseEntity.ok(ApiResponse.success("Patient fetched successfully", patient));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(\'ADMIN\') or @patientService.isOwner(#id, authentication.name)")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatientProfile(@PathVariable Long id, @Valid @RequestBody PatientProfileRequest request) {
        PatientResponse updated = patientService.updatePatientProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Patient profile updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(\'ADMIN\')")
    public ResponseEntity<ApiResponse<Object>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Patient deleted successfully"));
    }

    public PatientController(final PatientService patientService) {
        this.patientService = patientService;
    }
}

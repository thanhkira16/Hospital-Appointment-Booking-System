package com.fpt.hospital_appointment.controller;

import com.fpt.hospital_appointment.dto.ApiResponse;
import com.fpt.hospital_appointment.dto.response.DaySlotResponse;
import com.fpt.hospital_appointment.service.SlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/doctors/{doctorId}/day-schedule")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScheduleController {
    private final SlotService slotService;

    @GetMapping
    public ResponseEntity<ApiResponse<DaySlotResponse>> getDaySchedule(@PathVariable Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("Day schedule fetched successfully", slotService.getAdminDay(doctorId, date)));
    }

    public AdminScheduleController(final SlotService slotService) {
        this.slotService = slotService;
    }
}

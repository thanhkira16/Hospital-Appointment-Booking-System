package com.fpt.hospital_appointment.controller;

import com.fpt.hospital_appointment.dto.ApiResponse;
import com.fpt.hospital_appointment.dto.response.DaySlotResponse;
import com.fpt.hospital_appointment.dto.response.SlotResponse;
import com.fpt.hospital_appointment.service.SlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/doctors/{doctorId}/slots")
public class SlotController {
    private final SlotService slotService;

    @GetMapping
    public ResponseEntity<ApiResponse<SlotResponse>> getAvailableSlots(@PathVariable Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        SlotResponse response = slotService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(ApiResponse.success("Available slots fetched successfully", response));
    }

    @GetMapping("/day")
    public ResponseEntity<ApiResponse<DaySlotResponse>> getDay(@PathVariable Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("Day slots fetched successfully", slotService.getPublicDay(doctorId, date)));
    }

    public SlotController(final SlotService slotService) {
        this.slotService = slotService;
    }
}

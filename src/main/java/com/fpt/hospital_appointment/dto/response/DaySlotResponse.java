package com.fpt.hospital_appointment.dto.response;

import com.fpt.hospital_appointment.model.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Full picture of one doctor's day: every slot with its state, so the UI can
 * show free slots AND who has taken the busy ones.
 */
public record DaySlotResponse(
        Long doctorId,
        LocalDate date,
        Integer slotDurationMinutes,
        List<Slot> slots) {

    public enum SlotState { AVAILABLE, BOOKED, PAST }

    public record Slot(LocalTime startTime, LocalTime endTime, SlotState state, Booking booking) {
    }

    /** Patient details are null for the public view; only admin sees them. */
    public record Booking(Long appointmentId, AppointmentStatus status, Long patientId, String patientName,
                          String reasonForVisit) {
    }
}

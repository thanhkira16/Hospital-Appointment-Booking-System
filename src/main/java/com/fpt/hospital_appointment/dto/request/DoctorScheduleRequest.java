package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorScheduleRequest {
    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    @NotNull(message = "Slot duration is required")
    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    private Integer slotDurationMinutes;

    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public Integer getSlotDurationMinutes() {
        return this.slotDurationMinutes;
    }

    public void setDayOfWeek(final DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(final LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setSlotDurationMinutes(final Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public DoctorScheduleRequest() {
    }

    public DoctorScheduleRequest(final DayOfWeek dayOfWeek, final LocalTime startTime, final LocalTime endTime, final Integer slotDurationMinutes) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDurationMinutes = slotDurationMinutes;
    }
}

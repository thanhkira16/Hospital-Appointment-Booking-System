package com.fpt.hospital_appointment.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorScheduleResponse {
    private Long id;
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private boolean active;


    public static class DoctorScheduleResponseBuilder {
        private Long id;
        private Long doctorId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer slotDurationMinutes;
        private boolean active;

        DoctorScheduleResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public DoctorScheduleResponse.DoctorScheduleResponseBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorScheduleResponse.DoctorScheduleResponseBuilder doctorId(final Long doctorId) {
            this.doctorId = doctorId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorScheduleResponse.DoctorScheduleResponseBuilder dayOfWeek(final DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorScheduleResponse.DoctorScheduleResponseBuilder startTime(final LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorScheduleResponse.DoctorScheduleResponseBuilder endTime(final LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorScheduleResponse.DoctorScheduleResponseBuilder slotDurationMinutes(final Integer slotDurationMinutes) {
            this.slotDurationMinutes = slotDurationMinutes;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorScheduleResponse.DoctorScheduleResponseBuilder active(final boolean active) {
            this.active = active;
            return this;
        }

        public DoctorScheduleResponse build() {
            return new DoctorScheduleResponse(this.id, this.doctorId, this.dayOfWeek, this.startTime, this.endTime, this.slotDurationMinutes, this.active);
        }

        @Override
        public String toString() {
            return "DoctorScheduleResponse.DoctorScheduleResponseBuilder(id=" + this.id + ", doctorId=" + this.doctorId + ", dayOfWeek=" + this.dayOfWeek + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", slotDurationMinutes=" + this.slotDurationMinutes + ", active=" + this.active + ")";
        }
    }

    public static DoctorScheduleResponse.DoctorScheduleResponseBuilder builder() {
        return new DoctorScheduleResponse.DoctorScheduleResponseBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

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

    public boolean isActive() {
        return this.active;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setDoctorId(final Long doctorId) {
        this.doctorId = doctorId;
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

    public void setActive(final boolean active) {
        this.active = active;
    }

    public DoctorScheduleResponse() {
    }

    public DoctorScheduleResponse(final Long id, final Long doctorId, final DayOfWeek dayOfWeek, final LocalTime startTime, final LocalTime endTime, final Integer slotDurationMinutes, final boolean active) {
        this.id = id;
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDurationMinutes = slotDurationMinutes;
        this.active = active;
    }
}

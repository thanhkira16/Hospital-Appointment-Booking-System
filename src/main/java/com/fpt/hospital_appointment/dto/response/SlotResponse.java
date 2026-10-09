package com.fpt.hospital_appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Response shape for GET /api/slots - tells the client exactly which
 * times are bookable for a given doctor on a given date.
 */
public class SlotResponse {
    private Long doctorId;
    private LocalDate date;
    private Integer slotDurationMinutes;
    private List<LocalTime> availableSlots;


    public static class SlotResponseBuilder {
        private Long doctorId;
        private LocalDate date;
        private Integer slotDurationMinutes;
        private List<LocalTime> availableSlots;

        SlotResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public SlotResponse.SlotResponseBuilder doctorId(final Long doctorId) {
            this.doctorId = doctorId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public SlotResponse.SlotResponseBuilder date(final LocalDate date) {
            this.date = date;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public SlotResponse.SlotResponseBuilder slotDurationMinutes(final Integer slotDurationMinutes) {
            this.slotDurationMinutes = slotDurationMinutes;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public SlotResponse.SlotResponseBuilder availableSlots(final List<LocalTime> availableSlots) {
            this.availableSlots = availableSlots;
            return this;
        }

        public SlotResponse build() {
            return new SlotResponse(this.doctorId, this.date, this.slotDurationMinutes, this.availableSlots);
        }

        @Override
        public String toString() {
            return "SlotResponse.SlotResponseBuilder(doctorId=" + this.doctorId + ", date=" + this.date + ", slotDurationMinutes=" + this.slotDurationMinutes + ", availableSlots=" + this.availableSlots + ")";
        }
    }

    public static SlotResponse.SlotResponseBuilder builder() {
        return new SlotResponse.SlotResponseBuilder();
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Integer getSlotDurationMinutes() {
        return this.slotDurationMinutes;
    }

    public List<LocalTime> getAvailableSlots() {
        return this.availableSlots;
    }

    public void setDoctorId(final Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public void setSlotDurationMinutes(final Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public void setAvailableSlots(final List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public SlotResponse() {
    }

    public SlotResponse(final Long doctorId, final LocalDate date, final Integer slotDurationMinutes, final List<LocalTime> availableSlots) {
        this.doctorId = doctorId;
        this.date = date;
        this.slotDurationMinutes = slotDurationMinutes;
        this.availableSlots = availableSlots;
    }
}

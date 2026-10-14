package com.fpt.hospital_appointment.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedules")
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;
    @Column(nullable = false)
    private Integer slotDurationMinutes;
    @Column(nullable = false)
    private boolean active;

    private static boolean $default$active() {
        return true;
    }


    public static class DoctorScheduleBuilder {
        private Long id;
        private Doctor doctor;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer slotDurationMinutes;
        private boolean active$set;
        private boolean active$value;

        DoctorScheduleBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public DoctorSchedule.DoctorScheduleBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorSchedule.DoctorScheduleBuilder doctor(final Doctor doctor) {
            this.doctor = doctor;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorSchedule.DoctorScheduleBuilder dayOfWeek(final DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorSchedule.DoctorScheduleBuilder startTime(final LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorSchedule.DoctorScheduleBuilder endTime(final LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorSchedule.DoctorScheduleBuilder slotDurationMinutes(final Integer slotDurationMinutes) {
            this.slotDurationMinutes = slotDurationMinutes;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public DoctorSchedule.DoctorScheduleBuilder active(final boolean active) {
            this.active$value = active;
            active$set = true;
            return this;
        }

        public DoctorSchedule build() {
            boolean active$value = this.active$value;
            if (!this.active$set) active$value = DoctorSchedule.$default$active();
            return new DoctorSchedule(this.id, this.doctor, this.dayOfWeek, this.startTime, this.endTime, this.slotDurationMinutes, active$value);
        }

        @Override
        public String toString() {
            return "DoctorSchedule.DoctorScheduleBuilder(id=" + this.id + ", doctor=" + this.doctor + ", dayOfWeek=" + this.dayOfWeek + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", slotDurationMinutes=" + this.slotDurationMinutes + ", active$value=" + this.active$value + ")";
        }
    }

    public static DoctorSchedule.DoctorScheduleBuilder builder() {
        return new DoctorSchedule.DoctorScheduleBuilder();
    }

    public DoctorSchedule.DoctorScheduleBuilder toBuilder() {
        return new DoctorSchedule.DoctorScheduleBuilder().id(this.id).doctor(this.doctor).dayOfWeek(this.dayOfWeek).startTime(this.startTime).endTime(this.endTime).slotDurationMinutes(this.slotDurationMinutes).active(this.active);
    }

    public Long getId() {
        return this.id;
    }

    public Doctor getDoctor() {
        return this.doctor;
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

    public void setDoctor(final Doctor doctor) {
        this.doctor = doctor;
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

    public DoctorSchedule() {
        this.active = DoctorSchedule.$default$active();
    }

    public DoctorSchedule(final Long id, final Doctor doctor, final DayOfWeek dayOfWeek, final LocalTime startTime, final LocalTime endTime, final Integer slotDurationMinutes, final boolean active) {
        this.id = id;
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDurationMinutes = slotDurationMinutes;
        this.active = active;
    }
}

package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.dto.response.DaySlotResponse;
import com.fpt.hospital_appointment.dto.response.SlotResponse;
import com.fpt.hospital_appointment.exception.InvalidRequestException;
import com.fpt.hospital_appointment.exception.SlotUnavailableException;
import com.fpt.hospital_appointment.model.Appointment;
import com.fpt.hospital_appointment.model.AppointmentStatus;
import com.fpt.hospital_appointment.model.DoctorSchedule;
import com.fpt.hospital_appointment.repository.AppointmentRepository;
import com.fpt.hospital_appointment.repository.DoctorScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * THE core business logic of the app: computes the state of every time slot
 * of a doctor on a given date, by combining their weekly DoctorSchedule
 * windows with existing Appointment bookings.
 *
 * The patient-facing "available slots" endpoint, the admin "who is booked
 * when" view AND the internal "is this slot actually bookable" check used by
 * AppointmentService all go through buildDay(), so what is shown always
 * matches what booking will accept.
 */
@Service
public class SlotService {
    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;

    @Transactional(readOnly = true)
    public SlotResponse getAvailableSlots(Long doctorId, LocalDate date) {
        doctorService.findDoctorOrThrow(doctorId); // 404 if doctor doesn't exist
        rejectPastDate(date);
        DaySlotResponse day = buildDay(doctorId, date, false);
        List<LocalTime> available = day.slots().stream()
                .filter(slot -> slot.state() == DaySlotResponse.SlotState.AVAILABLE)
                .map(DaySlotResponse.Slot::startTime)
                .toList();
        return SlotResponse.builder().doctorId(doctorId).date(date).slotDurationMinutes(day.slotDurationMinutes()).availableSlots(available).build();
    }

    /** Public day view for patients: free and taken slots, without patient details. */
    @Transactional(readOnly = true)
    public DaySlotResponse getPublicDay(Long doctorId, LocalDate date) {
        doctorService.findDoctorOrThrow(doctorId);
        rejectPastDate(date);
        return buildDay(doctorId, date, false);
    }

    /** Admin day view: any date, with the patient who holds each taken slot. */
    @Transactional(readOnly = true)
    public DaySlotResponse getAdminDay(Long doctorId, LocalDate date) {
        doctorService.findDoctorOrThrow(doctorId);
        return buildDay(doctorId, date, true);
    }

    private void rejectPastDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Cannot fetch slots for a past date");
        }
    }

    /**
     * Slices the doctor's active windows into slots and marks each one BOOKED
     * when a non-cancelled appointment starts inside it (a cancelled one frees
     * the slot again).
     */
    private DaySlotResponse buildDay(Long doctorId, LocalDate date, boolean withPatients) {
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(doctorId, date.getDayOfWeek());
        List<Appointment> active = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date).stream()
                .filter(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED)
                .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                .toList();
        boolean isToday = date.isEqual(LocalDate.now());
        LocalTime now = LocalTime.now();
        Map<LocalTime, DaySlotResponse.Slot> slots = new TreeMap<>();
        Set<Appointment> matched = new HashSet<>();
        Integer slotDuration = null;
        for (DoctorSchedule schedule : schedules) {
            slotDuration = schedule.getSlotDurationMinutes(); // assumes one duration per doctor for simplicity
            if (slotDuration == null || slotDuration <= 0) {
                throw new InvalidRequestException("Slot duration must be positive");
            }
            LocalDateTime start = date.atTime(schedule.getStartTime());
            LocalDateTime windowEnd = date.atTime(schedule.getEndTime());
            while (!start.plusMinutes(slotDuration).isAfter(windowEnd)) {
                LocalTime from = start.toLocalTime();
                LocalTime to = start.plusMinutes(slotDuration).toLocalTime();
                if (!slots.containsKey(from)) {
                    Appointment holder = active.stream()
                            .filter(a -> !a.getAppointmentTime().isBefore(from) && a.getAppointmentTime().isBefore(to))
                            .findFirst().orElse(null);
                    if (holder != null) {
                        matched.add(holder);
                        slots.put(from, new DaySlotResponse.Slot(from, to, DaySlotResponse.SlotState.BOOKED, booking(holder, withPatients)));
                    } else {
                        boolean past = isToday && from.isBefore(now);
                        slots.put(from, new DaySlotResponse.Slot(from, to, past ? DaySlotResponse.SlotState.PAST : DaySlotResponse.SlotState.AVAILABLE, null));
                    }
                }
                start = start.plusMinutes(slotDuration);
            }
        }
        // Appointments that no longer sit on the grid (schedule edited afterwards) still occupy the doctor.
        for (Appointment orphan : active) {
            LocalTime from = orphan.getAppointmentTime();
            if (!matched.contains(orphan) && !slots.containsKey(from)) {
                int length = slotDuration == null ? 30 : slotDuration;
                slots.put(from, new DaySlotResponse.Slot(from, from.plusMinutes(length), DaySlotResponse.SlotState.BOOKED, booking(orphan, withPatients)));
            }
        }
        return new DaySlotResponse(doctorId, date, slotDuration, new ArrayList<>(slots.values()));
    }

    private DaySlotResponse.Booking booking(Appointment appointment, boolean withPatients) {
        if (!withPatients) {
            return null;
        }
        return new DaySlotResponse.Booking(appointment.getId(), appointment.getStatus(), appointment.getPatient().getId(),
                appointment.getPatient().getUser().getFullName(), appointment.getReasonForVisit());
    }

    /**
     * Called by AppointmentService right before saving a new booking. Reuses
     * getAvailableSlots() so the exact same rules apply - if it's not in the
     * available list, it's not bookable, full stop.
     */
    @Transactional(readOnly = true)
    public void validateSlotAvailability(Long doctorId, LocalDate date, LocalTime time) {
        SlotResponse slots = getAvailableSlots(doctorId, date);
        if (!slots.getAvailableSlots().contains(time)) {
            throw new SlotUnavailableException("The selected time slot (" + time + " on " + date + ") is not available for this doctor");
        }
    }

    public SlotService(final DoctorScheduleRepository scheduleRepository, final AppointmentRepository appointmentRepository, final DoctorService doctorService) {
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
    }
}

package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.dto.request.DoctorScheduleRequest;
import com.fpt.hospital_appointment.dto.response.DoctorScheduleResponse;
import com.fpt.hospital_appointment.exception.InvalidRequestException;
import com.fpt.hospital_appointment.exception.ResourceNotFoundException;
import com.fpt.hospital_appointment.model.Doctor;
import com.fpt.hospital_appointment.model.DoctorSchedule;
import com.fpt.hospital_appointment.repository.DoctorScheduleRepository;
import com.fpt.hospital_appointment.service.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DoctorScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final DoctorService doctorService;

    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> getScheduleByDoctorId(Long doctorId) {
        doctorService.findDoctorOrThrow(doctorId);
        return scheduleRepository.findByDoctorId(doctorId).stream().map(scheduleMapper::toResponse).toList();
    }

    @Transactional
    public DoctorScheduleResponse createSchedule(Long doctorId, DoctorScheduleRequest request) {
        Doctor doctor = doctorService.findDoctorOrThrow(doctorId);
        validateTimeRange(request);
        validateNoOverlap(doctorId, null, request);
        DoctorSchedule schedule = DoctorSchedule.builder().doctor(doctor).dayOfWeek(request.getDayOfWeek()).startTime(request.getStartTime()).endTime(request.getEndTime()).slotDurationMinutes(request.getSlotDurationMinutes()).active(true).build();
        DoctorSchedule saved = scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(saved);
    }

    @Transactional
    public DoctorScheduleResponse updateSchedule(Long scheduleId, DoctorScheduleRequest request) {
        DoctorSchedule schedule = findScheduleOrThrow(scheduleId);
        validateTimeRange(request);
        validateNoOverlap(schedule.getDoctor().getId(), scheduleId, request);
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setSlotDurationMinutes(request.getSlotDurationMinutes());
        DoctorSchedule updated = scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(updated);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        DoctorSchedule schedule = findScheduleOrThrow(scheduleId);
        scheduleRepository.delete(schedule);
    }

    // ---------- Helpers ----------
    @Transactional(readOnly = true)
    public DoctorSchedule findScheduleOrThrow(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(() -> new ResourceNotFoundException("DoctorSchedule", scheduleId));
    }

    @Transactional(readOnly = true)
    public boolean isScheduleOwner(Long scheduleId, String currentUserEmail) {
        return scheduleRepository.findById(scheduleId).map(schedule -> schedule.getDoctor().getUser().getEmail().equals(currentUserEmail)).orElse(false);
    }

    private void validateNoOverlap(Long doctorId, Long excludedId, DoctorScheduleRequest request) {
        // Serialize writes for the same doctor, including concurrent first inserts.
        scheduleRepository.lockDoctorById(doctorId);
        boolean overlaps = scheduleRepository.findActiveForUpdate(doctorId, request.getDayOfWeek())
                .stream()
                .filter(existing -> !java.util.Objects.equals(existing.getId(), excludedId))
                .anyMatch(existing -> request.getStartTime().isBefore(existing.getEndTime())
                        && request.getEndTime().isAfter(existing.getStartTime()));
        if (overlaps) {
            throw new com.fpt.hospital_appointment.exception.DuplicateResourceException(
                    "Lịch làm việc bị trùng hoặc chồng giờ trong cùng ngày. Vui lòng chọn khung giờ khác.");
        }
    }

    private void validateTimeRange(DoctorScheduleRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }
        if (request.getSlotDurationMinutes() == null || request.getSlotDurationMinutes() < 5
                || java.time.Duration.between(request.getStartTime(), request.getEndTime()).toMinutes() < request.getSlotDurationMinutes()) {
            throw new InvalidRequestException("Thời lượng mỗi lượt khám phải từ 5 phút và không vượt quá khung giờ làm việc.");
        }
    }

    public DoctorScheduleService(final DoctorScheduleRepository scheduleRepository, final ScheduleMapper scheduleMapper, final DoctorService doctorService) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.doctorService = doctorService;
    }
}

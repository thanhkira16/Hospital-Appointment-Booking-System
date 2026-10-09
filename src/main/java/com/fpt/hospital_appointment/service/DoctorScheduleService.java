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
        DoctorSchedule schedule = DoctorSchedule.builder().doctor(doctor).dayOfWeek(request.getDayOfWeek()).startTime(request.getStartTime()).endTime(request.getEndTime()).slotDurationMinutes(request.getSlotDurationMinutes()).active(true).build();
        DoctorSchedule saved = scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(saved);
    }

    @Transactional
    public DoctorScheduleResponse updateSchedule(Long scheduleId, DoctorScheduleRequest request) {
        DoctorSchedule schedule = findScheduleOrThrow(scheduleId);
        validateTimeRange(request);
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

    private void validateTimeRange(DoctorScheduleRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new InvalidRequestException("Start time must be before end time");
        }
    }

    public DoctorScheduleService(final DoctorScheduleRepository scheduleRepository, final ScheduleMapper scheduleMapper, final DoctorService doctorService) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.doctorService = doctorService;
    }
}

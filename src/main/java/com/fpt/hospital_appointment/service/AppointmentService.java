package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.dto.request.AppointmentRequest;
import com.fpt.hospital_appointment.dto.response.AppointmentResponse;
import com.fpt.hospital_appointment.exception.InvalidRequestException;
import com.fpt.hospital_appointment.exception.ResourceNotFoundException;
import com.fpt.hospital_appointment.exception.SlotUnavailableException;
import com.fpt.hospital_appointment.model.Appointment;
import com.fpt.hospital_appointment.model.AppointmentStatus;
import com.fpt.hospital_appointment.model.Doctor;
import com.fpt.hospital_appointment.model.Patient;
import com.fpt.hospital_appointment.repository.AppointmentRepository;
import com.fpt.hospital_appointment.service.mapper.AppointmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fpt.hospital_appointment.dto.PageResponse;
import com.fpt.hospital_appointment.service.specification.AppointmentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final SlotService slotService;
    private final EmailService emailService;

    @Transactional
    public AppointmentResponse bookAppointment(Long patientId, AppointmentRequest request) {
        // Lock before any reads so availability sees the preceding booking's commit.
        appointmentRepository.lockDoctor(request.getDoctorId());
        Doctor doctor = doctorService.findDoctorOrThrow(request.getDoctorId());
        Patient patient = patientService.findPatientOrThrow(patientId);
        slotService.validateSlotAvailability(doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime());
        if (appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(patientId, request.getAppointmentDate(), request.getAppointmentTime(), AppointmentStatus.CANCELLED)) {
            throw new SlotUnavailableException("Bạn đã có một lịch hẹn khác vào đúng giờ này.");
        }
        Appointment appointment = Appointment.builder().doctor(doctor).patient(patient).appointmentDate(request.getAppointmentDate()).appointmentTime(request.getAppointmentTime()).status(AppointmentStatus.PENDING).reasonForVisit(request.getReasonForVisit()).build();
        Appointment saved = appointmentRepository.save(appointment);
        emailService.sendBookingConfirmation(saved); // fire-and-forget, async
        return appointmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        return appointmentMapper.toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public com.fpt.hospital_appointment.dto.PageResponse<AppointmentResponse> getAppointmentsByPatient(Long patientId, org.springframework.data.domain.Pageable pageable) {
        patientService.findPatientOrThrow(patientId);
        org.springframework.data.domain.Page<AppointmentResponse> page = appointmentRepository.findByPatientId(patientId, pageable).map(appointmentMapper::toResponse);
        return com.fpt.hospital_appointment.dto.PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public com.fpt.hospital_appointment.dto.PageResponse<AppointmentResponse> getAppointmentsByDoctor(Long doctorId, org.springframework.data.domain.Pageable pageable) {
        doctorService.findDoctorOrThrow(doctorId);
        org.springframework.data.domain.Page<AppointmentResponse> page = appointmentRepository.findByDoctorId(doctorId, pageable).map(appointmentMapper::toResponse);
        return com.fpt.hospital_appointment.dto.PageResponse.from(page);
    }

    // ---------- Helpers ----------
    @Transactional(readOnly = true)
    public Appointment findAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    @Transactional(readOnly = true)
    public boolean isPatientOwner(Long appointmentId, String currentUserEmail) {
        return appointmentRepository.findById(appointmentId).map(appointment -> appointment.getPatient().getUser().getEmail().equals(currentUserEmail)).orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isDoctorOwner(Long appointmentId, String currentUserEmail) {
        return appointmentRepository.findById(appointmentId).map(appointment -> appointment.getDoctor().getUser().getEmail().equals(currentUserEmail)).orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isPatientSelf(Long patientId, String currentUserEmail) {
        return patientService.isOwner(patientId, currentUserEmail);
    }

    // ---------- Status transitions ----------
    @Transactional
    public AppointmentResponse confirmAppointment(Long appointmentId) {
        Appointment appointment = findAppointmentForUpdate(appointmentId);
        validateStatusTransition(appointment.getStatus(), AppointmentStatus.CONFIRMED);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment updated = appointmentRepository.save(appointment);
        emailService.sendConfirmationNotice(updated);
        return appointmentMapper.toResponse(updated);
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long appointmentId) {
        Appointment appointment = findAppointmentForUpdate(appointmentId);
        validateStatusTransition(appointment.getStatus(), AppointmentStatus.COMPLETED);
        if (appointment.getAppointmentDate().atTime(appointment.getAppointmentTime()).isAfter(java.time.LocalDateTime.now())) {
            throw new InvalidRequestException("Chưa đến giờ khám, không thể đánh dấu cuộc hẹn hoàn thành.");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment updated = appointmentRepository.save(appointment);
        emailService.sendCompletionNotice(updated);
        return appointmentMapper.toResponse(updated);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, String cancellationReason) {
        Appointment appointment = findAppointmentForUpdate(appointmentId);
        validateStatusTransition(appointment.getStatus(), AppointmentStatus.CANCELLED);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancellationReason);
        Appointment updated = appointmentRepository.save(appointment);
        emailService.sendCancellationNotice(updated);
        return appointmentMapper.toResponse(updated);
    }

    @Transactional
    public Appointment findAppointmentForUpdate(Long id) {
        return appointmentRepository.findForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus target) {
        boolean isValid = switch (target) {
            case CONFIRMED -> current == AppointmentStatus.PENDING;
            case COMPLETED -> current == AppointmentStatus.CONFIRMED;
            case CANCELLED -> current == AppointmentStatus.PENDING || current == AppointmentStatus.CONFIRMED;
            case PENDING -> false;
        };
        if (!isValid) {
            throw new InvalidRequestException("Cannot change appointment status from " + current + " to " + target);
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> searchAppointments(AppointmentStatus status, LocalDate fromDate, LocalDate toDate, Long doctorId, Long patientId, Pageable pageable) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new InvalidRequestException("Ngày bắt đầu không được sau ngày kết thúc.");
        }
        var spec = AppointmentSpecification.buildSearchSpecification(status, fromDate, toDate, doctorId, patientId);
        Page<AppointmentResponse> page = appointmentRepository.findAll(spec, pageable).map(appointmentMapper::toResponse);
        return PageResponse.from(page);
    }

    public AppointmentService(final AppointmentRepository appointmentRepository, final AppointmentMapper appointmentMapper, final DoctorService doctorService, final PatientService patientService, final SlotService slotService, final EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.slotService = slotService;
        this.emailService = emailService;
    }
}

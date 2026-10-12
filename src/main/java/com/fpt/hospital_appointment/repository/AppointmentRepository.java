package com.fpt.hospital_appointment.repository;

import com.fpt.hospital_appointment.model.Appointment;
import com.fpt.hospital_appointment.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>,
        JpaSpecificationExecutor<Appointment> {

    @org.springframework.data.jpa.repository.Query(value = "select id from doctors where id = :doctorId for update", nativeQuery = true)
    Optional<Long> lockDoctor(@org.springframework.data.repository.query.Param("doctorId") Long doctorId);

    @org.springframework.data.jpa.repository.Query(value = "select * from appointments where id = :id for update", nativeQuery = true)
    Optional<Appointment> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);

    Page<Appointment> findByPatientId(Long id, Pageable pageable);

    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    List<Appointment> findByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus excludedStatus
    );

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long patientId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus excludedStatus
    );

    Optional<Appointment> findByIdAndPatientId(Long id, Long patientId);

    Optional<Appointment> findByIdAndDoctorId(Long id, Long doctorId);
}

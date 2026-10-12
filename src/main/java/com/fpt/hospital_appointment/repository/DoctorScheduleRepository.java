package com.fpt.hospital_appointment.repository;

import com.fpt.hospital_appointment.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule,Long> {

    @org.springframework.data.jpa.repository.Query(value = "select * from doctors where id = :doctorId for update", nativeQuery = true)
    java.util.Optional<com.fpt.hospital_appointment.model.Doctor> lockDoctorById(@org.springframework.data.repository.query.Param("doctorId") Long doctorId);

    @org.springframework.data.jpa.repository.Query(value = "select * from doctor_schedules where doctor_id = :doctorId and day_of_week = :#{#day.name()} and active = true for update", nativeQuery = true)
    List<DoctorSchedule> findActiveForUpdate(@org.springframework.data.repository.query.Param("doctorId") Long doctorId,
            @org.springframework.data.repository.query.Param("day") DayOfWeek day);

    List<DoctorSchedule> findByDoctorId(Long doctorId);

    List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
}

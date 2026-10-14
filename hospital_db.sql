-- Complete database setup and idempotent sample data for Hospital Appointment Booking System.
-- Demo password for every account: Password123!
-- Import this file once to create hospital_db and seed its tables.

CREATE DATABASE IF NOT EXISTS hospital_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_db;

START TRANSACTION;

SET @demo_password = '$2a$10$AJuqlXVv4dTs5OcfQmIAi.sVJyUBSeehmwr5kxf0KM7SoSNm4gcSC';

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'System Administrator', 'thanhtch.22git@vku.udn.vn', @demo_password, 'ADMIN', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'thanhtch.22git@vku.udn.vn');

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'Nguyen Minh Anh', 'minh.anh.doctor@hospital.test', @demo_password, 'DOCTOR', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'minh.anh.doctor@hospital.test');

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'Tran Quoc Bao', 'quoc.bao.doctor@hospital.test', @demo_password, 'DOCTOR', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'quoc.bao.doctor@hospital.test');

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'Bác sĩ Nguyễn An', 'hthanh16092004@gmail.com', @demo_password, 'DOCTOR', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'hthanh16092004@gmail.com');

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'Le Thu Ha', 'thu.ha.patient@hospital.test', @demo_password, 'PATIENT', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'thu.ha.patient@hospital.test');

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'Pham Gia Huy', 'gia.huy.patient@hospital.test', @demo_password, 'PATIENT', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'gia.huy.patient@hospital.test');

INSERT INTO doctors (user_id, specialization, qualification, years_of_experience, phone_number, consultation_fee)
SELECT u.id, 'Cardiology', 'MD, Cardiology', 12, '0901000001', 500000
FROM users u
WHERE u.email = 'minh.anh.doctor@hospital.test'
  AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id);

INSERT INTO doctors (user_id, specialization, qualification, years_of_experience, phone_number, consultation_fee)
SELECT u.id, 'Dermatology', 'MD, Dermatology', 8, '0901000002', 400000
FROM users u
WHERE u.email = 'quoc.bao.doctor@hospital.test'
  AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id);

INSERT INTO doctors (user_id, specialization, qualification, years_of_experience, phone_number, consultation_fee)
SELECT u.id, 'Nội tổng quát', 'Bác sĩ chuyên khoa I', 5, '0901234567', 300000
FROM users u
WHERE u.email = 'hthanh16092004@gmail.com'
  AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id);

UPDATE doctors d
JOIN users u ON u.id = d.user_id
SET d.specialization = 'Nội tổng quát',
    d.qualification = 'Bác sĩ chuyên khoa I',
    d.years_of_experience = 5,
    d.phone_number = '0901234567',
    d.consultation_fee = 300000
WHERE u.email = 'hthanh16092004@gmail.com';

INSERT INTO patients (user_id, date_of_birth, gender, phone_number, address, medical_history)
SELECT u.id, '1992-04-18', 'Female', '0902000001', 'District 1, Ho Chi Minh City', 'No known drug allergies.'
FROM users u
WHERE u.email = 'thu.ha.patient@hospital.test'
  AND NOT EXISTS (SELECT 1 FROM patients p WHERE p.user_id = u.id);

INSERT INTO patients (user_id, date_of_birth, gender, phone_number, address, medical_history)
SELECT u.id, '1987-11-03', 'Male', '0902000002', 'Binh Thanh District, Ho Chi Minh City', 'Seasonal allergic rhinitis.'
FROM users u
WHERE u.email = 'gia.huy.patient@hospital.test'
  AND NOT EXISTS (SELECT 1 FROM patients p WHERE p.user_id = u.id);

INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes, active)
SELECT d.id, days.day_of_week, '09:00:00', '17:00:00', 30, b'1'
FROM doctors d
JOIN users u ON u.id = d.user_id
CROSS JOIN (
    SELECT 'MONDAY' AS day_of_week UNION ALL
    SELECT 'TUESDAY' UNION ALL
    SELECT 'WEDNESDAY' UNION ALL
    SELECT 'THURSDAY' UNION ALL
    SELECT 'FRIDAY'
) AS days
WHERE u.email IN ('minh.anh.doctor@hospital.test', 'quoc.bao.doctor@hospital.test')
  AND NOT EXISTS (
      SELECT 1 FROM doctor_schedules s
      WHERE s.doctor_id = d.id AND s.day_of_week = days.day_of_week
        AND s.start_time = '09:00:00' AND s.end_time = '17:00:00'
  );

INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes, active)
SELECT d.id, shifts.day_of_week, shifts.start_time, shifts.end_time, 30, b'1'
FROM doctors d
JOIN users u ON u.id = d.user_id AND u.email = 'hthanh16092004@gmail.com'
CROSS JOIN (
    SELECT 'MONDAY' AS day_of_week, '08:00:00' AS start_time, '12:00:00' AS end_time UNION ALL
    SELECT 'TUESDAY', '13:00:00', '17:00:00' UNION ALL
    SELECT 'WEDNESDAY', '08:00:00', '12:00:00' UNION ALL
    SELECT 'THURSDAY', '13:00:00', '17:00:00' UNION ALL
    SELECT 'FRIDAY', '08:00:00', '12:00:00' UNION ALL
    SELECT 'SATURDAY', '08:00:00', '11:00:00'
) AS shifts
WHERE NOT EXISTS (
    SELECT 1 FROM doctor_schedules s
    WHERE s.doctor_id = d.id AND s.day_of_week = shifts.day_of_week
      AND s.start_time = shifts.start_time AND s.end_time = shifts.end_time
);

SET @next_tuesday = DATE_ADD(CURDATE(), INTERVAL (MOD(1 - WEEKDAY(CURDATE()) + 7, 7) + IF(WEEKDAY(CURDATE()) = 1, 7, 0)) DAY);
SET @next_wednesday = DATE_ADD(CURDATE(), INTERVAL (MOD(2 - WEEKDAY(CURDATE()) + 7, 7) + IF(WEEKDAY(CURDATE()) = 2, 7, 0)) DAY);
SET @previous_tuesday = DATE_ADD(CURDATE(), INTERVAL (MOD(1 - WEEKDAY(CURDATE()) + 7, 7) - 7) DAY);
SET @previous_wednesday = DATE_ADD(CURDATE(), INTERVAL (MOD(2 - WEEKDAY(CURDATE()) + 7, 7) - 7) DAY);
SET @next_monday = DATE_ADD(CURDATE(), INTERVAL (MOD(0 - WEEKDAY(CURDATE()) + 7, 7) + IF(WEEKDAY(CURDATE()) = 0, 7, 0)) DAY);
SET @next_thursday = DATE_ADD(CURDATE(), INTERVAL (MOD(3 - WEEKDAY(CURDATE()) + 7, 7) + IF(WEEKDAY(CURDATE()) = 3, 7, 0)) DAY);
SET @previous_monday = DATE_ADD(CURDATE(), INTERVAL (MOD(0 - WEEKDAY(CURDATE()) + 7, 7) - 7) DAY);
SET @previous_thursday = DATE_ADD(CURDATE(), INTERVAL (MOD(3 - WEEKDAY(CURDATE()) + 7, 7) - 7) DAY);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, created_at, updated_at)
SELECT d.id, p.id, @previous_tuesday, '09:00:00', 'COMPLETED', 'Routine heart health follow-up', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'minh.anh.doctor@hospital.test'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'thu.ha.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @previous_tuesday AND a.appointment_time = '09:00:00'
);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, created_at, updated_at)
SELECT d.id, p.id, @next_tuesday, '09:30:00', 'CONFIRMED', 'Cardiology consultation', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'minh.anh.doctor@hospital.test'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'gia.huy.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @next_tuesday AND a.appointment_time = '09:30:00'
);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, created_at, updated_at)
SELECT d.id, p.id, @next_wednesday, '10:00:00', 'PENDING', 'Skin rash consultation', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'quoc.bao.doctor@hospital.test'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'thu.ha.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @next_wednesday AND a.appointment_time = '10:00:00'
);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, cancellation_reason, created_at, updated_at)
SELECT d.id, p.id, @previous_wednesday, '11:00:00', 'CANCELLED', 'Dermatology consultation', 'Patient requested rescheduling.', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'quoc.bao.doctor@hospital.test'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'gia.huy.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @previous_wednesday AND a.appointment_time = '11:00:00'
);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, created_at, updated_at)
SELECT d.id, p.id, @previous_monday, '08:30:00', 'COMPLETED', 'Khám tổng quát và đánh giá tình trạng mệt mỏi kéo dài', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'hthanh16092004@gmail.com'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'thu.ha.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @previous_monday AND a.appointment_time = '08:30:00'
);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, cancellation_reason, created_at, updated_at)
SELECT d.id, p.id, @previous_thursday, '14:00:00', 'CANCELLED', 'Tư vấn đau đầu và mất ngủ', 'Bệnh nhân bận công việc đột xuất.', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'hthanh16092004@gmail.com'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'gia.huy.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @previous_thursday AND a.appointment_time = '14:00:00'
);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, created_at, updated_at)
SELECT d.id, p.id, @next_monday, '09:00:00', 'CONFIRMED', 'Tái khám huyết áp và xem kết quả xét nghiệm', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'hthanh16092004@gmail.com'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'gia.huy.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @next_monday AND a.appointment_time = '09:00:00'
);

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason_for_visit, created_at, updated_at)
SELECT d.id, p.id, @next_thursday, '15:00:00', 'PENDING', 'Khám triệu chứng ho và đau họng', NOW(), NOW()
FROM doctors d
JOIN users du ON du.id = d.user_id AND du.email = 'hthanh16092004@gmail.com'
JOIN patients p ON p.user_id = (SELECT id FROM users WHERE email = 'thu.ha.patient@hospital.test')
WHERE NOT EXISTS (
    SELECT 1 FROM appointments a WHERE a.doctor_id = d.id AND a.patient_id = p.id
      AND a.appointment_date = @next_thursday AND a.appointment_time = '15:00:00'
);

INSERT INTO medical_records (appointment_id, diagnosis, prescription, doctor_notes, created_at)
SELECT a.id, 'Stable blood pressure; no acute cardiac findings.', 'Continue current medication as prescribed.', 'Follow-up in six months; maintain regular exercise.', NOW()
FROM appointments a
JOIN doctors d ON d.id = a.doctor_id
JOIN users du ON du.id = d.user_id AND du.email = 'minh.anh.doctor@hospital.test'
JOIN patients p ON p.id = a.patient_id
JOIN users pu ON pu.id = p.user_id AND pu.email = 'thu.ha.patient@hospital.test'
WHERE a.status = 'COMPLETED'
  AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.appointment_id = a.id);

INSERT INTO medical_records (appointment_id, diagnosis, prescription, doctor_notes, created_at)
SELECT a.id,
       'Suy nhược nhẹ do thiếu ngủ; chưa ghi nhận dấu hiệu bất thường cấp tính.',
       'Bổ sung vitamin nhóm B sau ăn sáng trong 14 ngày; uống đủ nước và nghỉ ngơi hợp lý.',
       'Theo dõi thêm trong hai tuần. Tái khám sớm nếu mệt tăng, sốt hoặc khó thở.',
       NOW()
FROM appointments a
JOIN doctors d ON d.id = a.doctor_id
JOIN users du ON du.id = d.user_id AND du.email = 'hthanh16092004@gmail.com'
JOIN patients p ON p.id = a.patient_id
JOIN users pu ON pu.id = p.user_id AND pu.email = 'thu.ha.patient@hospital.test'
WHERE a.status = 'COMPLETED'
  AND a.appointment_date = @previous_monday
  AND a.appointment_time = '08:30:00'
  AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.appointment_id = a.id);

COMMIT;

-- Idempotent sample data for the Hospital Appointment Booking System.
-- Demo password for every account below: Password123!
-- Run with: mysql -u root hospital_db < database/sample-data.sql

START TRANSACTION;

SET @demo_password = '$2a$10$AJuqlXVv4dTs5OcfQmIAi.sVJyUBSeehmwr5kxf0KM7SoSNm4gcSC';

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'System Administrator', 'admin@hospital.test', @demo_password, 'ADMIN', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@hospital.test');

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'Nguyen Minh Anh', 'minh.anh.doctor@hospital.test', @demo_password, 'DOCTOR', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'minh.anh.doctor@hospital.test');

INSERT INTO users (full_name, email, password, role, enabled, created_at)
SELECT 'Tran Quoc Bao', 'quoc.bao.doctor@hospital.test', @demo_password, 'DOCTOR', b'1', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'quoc.bao.doctor@hospital.test');

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

SET @next_tuesday = DATE_ADD(CURDATE(), INTERVAL (MOD(1 - WEEKDAY(CURDATE()) + 7, 7) + IF(WEEKDAY(CURDATE()) = 1, 7, 0)) DAY);
SET @next_wednesday = DATE_ADD(CURDATE(), INTERVAL (MOD(2 - WEEKDAY(CURDATE()) + 7, 7) + IF(WEEKDAY(CURDATE()) = 2, 7, 0)) DAY);
SET @previous_tuesday = DATE_ADD(CURDATE(), INTERVAL (MOD(1 - WEEKDAY(CURDATE()) + 7, 7) - 7) DAY);
SET @previous_wednesday = DATE_ADD(CURDATE(), INTERVAL (MOD(2 - WEEKDAY(CURDATE()) + 7, 7) - 7) DAY);

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

INSERT INTO medical_records (appointment_id, diagnosis, prescription, doctor_notes, created_at)
SELECT a.id, 'Stable blood pressure; no acute cardiac findings.', 'Continue current medication as prescribed.', 'Follow-up in six months; maintain regular exercise.', NOW()
FROM appointments a
JOIN doctors d ON d.id = a.doctor_id
JOIN users du ON du.id = d.user_id AND du.email = 'minh.anh.doctor@hospital.test'
JOIN patients p ON p.id = a.patient_id
JOIN users pu ON pu.id = p.user_id AND pu.email = 'thu.ha.patient@hospital.test'
WHERE a.status = 'COMPLETED'
  AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.appointment_id = a.id);

COMMIT;

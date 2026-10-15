package com.fpt.hospital_appointment.service;

import com.fpt.hospital_appointment.dto.request.AdminCreateDoctorRequest;
import com.fpt.hospital_appointment.dto.request.DoctorProfileRequest;
import com.fpt.hospital_appointment.dto.response.DoctorResponse;
import com.fpt.hospital_appointment.exception.DuplicateResourceException;
import com.fpt.hospital_appointment.exception.ResourceNotFoundException;
import com.fpt.hospital_appointment.model.Doctor;
import com.fpt.hospital_appointment.model.Role;
import com.fpt.hospital_appointment.model.User;
import com.fpt.hospital_appointment.repository.DoctorRepository;
import com.fpt.hospital_appointment.repository.UserRepository;
import com.fpt.hospital_appointment.service.mapper.DoctorMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fpt.hospital_appointment.dto.PageResponse;
import com.fpt.hospital_appointment.service.specification.DoctorSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorMapper doctorMapper;

    // Admin-driven doctor onboarding: creates the login account and the
    // doctor profile together, with an admin-chosen temporary password.
    @Transactional
    public DoctorResponse createDoctorAccount(AdminCreateDoctorRequest request) {
        String email = request.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered: " + email);
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DOCTOR)
                .enabled(true)
                .mustChangePassword(request.isRequirePasswordChange())
                .build();
        user = userRepository.save(user);
        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization(request.getSpecialization())
                .qualification(request.getQualification())
                .yearsOfExperience(request.getYearsOfExperience())
                .phoneNumber(request.getPhoneNumber())
                .consultationFee(request.getConsultationFee())
                .build();
        Doctor saved = doctorRepository.save(doctor);
        return doctorMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> getAllDoctors(Pageable pageable) {
        Page<DoctorResponse> page = doctorRepository.findAll(pageable).map(doctorMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = findDoctorOrThrow(id);
        return doctorMapper.toResponse(doctor);
    }

    @Transactional
    public DoctorResponse updateDoctorProfile(Long id, DoctorProfileRequest request) {
        Doctor doctor = findDoctorOrThrow(id);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setYearsOfExperience(request.getYearsOfExperience());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setConsultationFee(request.getConsultationFee());
        Doctor updated = doctorRepository.save(doctor);
        return doctorMapper.toResponse(updated);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctorOrThrow(id);
        doctorRepository.delete(doctor);
    }

    @Transactional(readOnly = true)
    public Doctor findDoctorOrThrow(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByUserEmail(email).orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for current user"));
        return doctorMapper.toResponse(doctor);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(Long doctorId, String currentUserEmail) {
        return doctorRepository.findById(doctorId).map(doctor -> doctor.getUser().getEmail().equals(currentUserEmail)).orElse(false);
    }

    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> searchDoctors(String specialization, Integer minExperience, Double maxFee, Pageable pageable) {
        var spec = DoctorSpecification.buildSearchSpecification(specialization, minExperience, maxFee);
        Page<DoctorResponse> page = doctorRepository.findAll(spec, pageable).map(doctorMapper::toResponse);
        return PageResponse.from(page);
    }

    public DoctorService(final DoctorRepository doctorRepository, final UserRepository userRepository, final PasswordEncoder passwordEncoder, final DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.doctorMapper = doctorMapper;
    }
}

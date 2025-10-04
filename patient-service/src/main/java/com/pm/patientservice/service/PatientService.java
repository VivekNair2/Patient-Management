package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.exceptiom.EmailAlreadyExistsException;
import com.pm.patientservice.exceptiom.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDto>  getPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponseDto> patientResponseDtos = patients.stream()
                                                       .map(patient-> PatientMapper.toDto(patient))
                                                       .toList();
        return patientResponseDtos;
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if(patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists" +
                    " " + patientRequestDto.getEmail());
        }

        Patient savedPatient = patientRepository.save(PatientMapper.toModel(patientRequestDto));
        return PatientMapper.toDto(savedPatient);
    }
    public PatientResponseDto updatePatient(UUID patientId, PatientRequestDto patientRequestDto) {
        Patient existingPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));
        if(patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists" +
                    " " + patientRequestDto.getEmail());
        }
        existingPatient.setName(patientRequestDto.getName());
        existingPatient.setEmail(patientRequestDto.getEmail());
        existingPatient.setAddress(patientRequestDto.getAddress());
        existingPatient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));
        Patient updatedPatient = patientRepository.save(existingPatient);
        return PatientMapper.toDto(updatedPatient);
    }
}

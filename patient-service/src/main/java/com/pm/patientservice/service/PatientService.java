package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
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
}

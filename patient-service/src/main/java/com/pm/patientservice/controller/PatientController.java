package com.pm.patientservice.controller;
import java.util.*;
import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/patients")
@Tag(name="Patient",description ="Api for managing patients")
public class PatientController {
    public final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
//    @Autowired
//    private PatientService patientService;

    @GetMapping
    @Operation(summary="Get all patients",description="Retrieve a list of all patients")
    public ResponseEntity<List<PatientResponseDto>> getPatients() {
        List<PatientResponseDto> patients=patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    @Operation(summary="Create a new patient",description="Create a new patient with the provided details")
    public ResponseEntity<PatientResponseDto> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDto patientRequestDto) {
        PatientResponseDto savedPatient=patientService.createPatient(patientRequestDto);
        return ResponseEntity.ok().body(savedPatient);
    }


    @PutMapping("/{id}")
    @Operation(summary="Update an existing patient",description="Update the details of an existing patient by ID")
    public ResponseEntity<PatientResponseDto> updatePatient(@PathVariable("id") UUID patientId,
                                                            @Validated({Default.class}) @RequestBody PatientRequestDto patientRequestDto) {
        PatientResponseDto updatedPatient=patientService.updatePatient(patientId, patientRequestDto);
        return ResponseEntity.ok().body(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary="Delete a patient",description="Delete an existing patient by ID")
    public ResponseEntity<Void> deletePatient(@PathVariable("id") UUID patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.noContent().build();
    }

}

package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email);
    //when u update a patient u should not get email already exists error . This method excludes the users email and checks if there are any other with the same email
    boolean existsByEmailAndIdNot(String email, UUID id);
}

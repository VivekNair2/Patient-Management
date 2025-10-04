package com.pm.patientservice.exceptiom;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message)
    {
        super(message);
    }
}

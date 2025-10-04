package com.pm.patientservice.exceptiom;

public class EmailAlreadyExistsException extends RuntimeException
{
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}

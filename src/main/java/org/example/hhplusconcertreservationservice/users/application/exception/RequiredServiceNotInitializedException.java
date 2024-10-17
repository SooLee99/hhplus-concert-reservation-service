package org.example.hhplusconcertreservationservice.users.application.exception;

public class RequiredServiceNotInitializedException extends RuntimeException {
    public RequiredServiceNotInitializedException() {
        super(ExceptionMessage.REQUIRED_SERVICE_NOT_INITIALIZED.getMessage());
    }
}

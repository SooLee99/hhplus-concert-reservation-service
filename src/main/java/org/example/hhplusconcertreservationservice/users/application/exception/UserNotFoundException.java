package org.example.hhplusconcertreservationservice.users.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(ExceptionMessage.USER_NOT_FOUND.getMessage());
    }
}

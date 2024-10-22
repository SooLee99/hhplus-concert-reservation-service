package org.example.hhplusconcertreservationservice.users.application.exception;

public class ActiveTokenExistsException extends RuntimeException {

    public ActiveTokenExistsException() {
        super(ExceptionMessage.ACTIVE_TOKEN_EXISTS.getMessage());
    }
}
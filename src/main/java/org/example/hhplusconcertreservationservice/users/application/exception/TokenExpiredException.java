package org.example.hhplusconcertreservationservice.users.application.exception;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super(ExceptionMessage.TOKEN_EXPIRED.getMessage());
    }
}
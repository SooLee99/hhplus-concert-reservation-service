package org.example.hhplusconcertreservationservice.reservations.application.exception;

public class ReservationCreationException extends RuntimeException {
    public ReservationCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
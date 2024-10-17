package org.example.hhplusconcertreservationservice.reservations.application.exception;

public class ReservationException extends RuntimeException {
    public ReservationException(String message) {
        super(message);
    }
}
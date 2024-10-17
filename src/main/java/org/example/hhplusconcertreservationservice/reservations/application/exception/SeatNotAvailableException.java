package org.example.hhplusconcertreservationservice.reservations.application.exception;

public class SeatNotAvailableException extends ReservationException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}
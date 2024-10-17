package org.example.hhplusconcertreservationservice.reservations.domain.exception;

public class ReservationNotAllowedException extends RuntimeException {

        public ReservationNotAllowedException(String message) {
            super(message);
        }
}

package org.example.hhplusconcertreservationservice.reservations.domain.exception;

/**
 * 예약 요청이 유효하지 않은 경우 발생하는 예외
 */
public class InvalidReservationException extends RuntimeException {

    private final ReservationErrorMessages errorMessages;

    public InvalidReservationException(ReservationErrorMessages errorMessages) {
        super(errorMessages.getMessage());
        this.errorMessages = errorMessages;
    }

    public ReservationErrorMessages getErrorMessages() {
        return errorMessages;
    }
}

package org.example.hhplusconcertreservationservice.reservations.application.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;

@Getter
@NoArgsConstructor
public class CreateReservationResponse {
    private Long reservationId;
    private Long userId;
    private Long scheduleId;
    private Long seatId;
    private String reservationStatus;

    public CreateReservationResponse(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.userId = reservation.getUserId();
        this.scheduleId = reservation.getScheduleId();
        this.seatId = reservation.getSeatId();
        this.reservationStatus = reservation.getReservationStatus().name();
    }
}
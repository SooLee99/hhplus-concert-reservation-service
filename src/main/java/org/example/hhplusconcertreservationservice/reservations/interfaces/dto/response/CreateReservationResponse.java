package org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationResponse {
    private Long reservationId;
    private Long userId;
    private Long seatId;
    private Long scheduleId;
    private String reservationStatus;

    public CreateReservationResponse(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.userId = reservation.getUserId();
        this.seatId = reservation.getSeatId();
        this.scheduleId = reservation.getScheduleId();
        this.reservationStatus = reservation.getReservationStatus().name();
    }
}

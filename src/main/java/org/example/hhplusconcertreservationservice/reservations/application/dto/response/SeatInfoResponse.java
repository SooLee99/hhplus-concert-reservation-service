package org.example.hhplusconcertreservationservice.reservations.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;

@Getter
@AllArgsConstructor
public class SeatInfoResponse {
    private Long seatId;
    private SeatStatus seatStatus;
}
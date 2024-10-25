package org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;

@Getter
@Builder
@AllArgsConstructor
public class SeatInfoResponse {
    private final Long seatId;
    private final int seatNumber;
    private final SeatStatus status;
}
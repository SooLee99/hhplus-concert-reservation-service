package org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;

@Getter
public class SeatInfoResponse {
    private Long seatId;
    private SeatStatus seatStatus;

    @Builder
    public SeatInfoResponse(Long seatId, SeatStatus seatStatus) {
        this.seatId = seatId;
        this.seatStatus = seatStatus;
    }
}
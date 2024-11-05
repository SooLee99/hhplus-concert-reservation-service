package org.example.hhplusconcertreservationservice.reservations.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatTypeInfoResponse {
    private final String seatTypeName;
    private final double price;
}

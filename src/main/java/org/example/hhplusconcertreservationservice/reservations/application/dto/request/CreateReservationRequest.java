package org.example.hhplusconcertreservationservice.reservations.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    private Long userId;
    private Long scheduleId;
    private Long seatId;
    private Long paymentId;
}

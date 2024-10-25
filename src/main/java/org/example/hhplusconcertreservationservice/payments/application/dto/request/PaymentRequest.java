package org.example.hhplusconcertreservationservice.payments.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRequest {
    private Long seatId;
    private String token;
}

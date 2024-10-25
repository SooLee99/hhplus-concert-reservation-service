package org.example.hhplusconcertreservationservice.payments.application.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long paymentId;
    private Long userId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paymentTime;

    public PaymentResponse(Long paymentId, Long userId, BigDecimal amount, String paymentStatus, LocalDateTime paymentTime) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }
}

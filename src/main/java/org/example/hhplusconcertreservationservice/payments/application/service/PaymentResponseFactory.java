package org.example.hhplusconcertreservationservice.payments.application.service;

import org.example.hhplusconcertreservationservice.payments.application.dto.response.PaymentResponse;
import org.example.hhplusconcertreservationservice.payments.domain.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentResponseFactory {

    public PaymentResponse createResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getPaymentStatus().name(),
                payment.getPaymentTime()
        );
    }
}

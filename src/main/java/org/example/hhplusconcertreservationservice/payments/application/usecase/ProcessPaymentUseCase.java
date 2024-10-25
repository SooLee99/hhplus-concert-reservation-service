package org.example.hhplusconcertreservationservice.payments.application.usecase;


import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.example.hhplusconcertreservationservice.payments.application.dto.response.PaymentResponse;

import java.util.List;

public interface ProcessPaymentUseCase {
    PaymentResponse processPayment(PaymentRequest request);

    List<PaymentResponse> getPaymentsByUserId(Long userId);
}

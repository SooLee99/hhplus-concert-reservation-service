package org.example.hhplusconcertreservationservice.payments.application.facade;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.example.hhplusconcertreservationservice.payments.application.dto.response.PaymentResponse;
import org.example.hhplusconcertreservationservice.payments.application.usecase.ProcessPaymentUseCase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final ProcessPaymentUseCase processPaymentUseCase;
    public PaymentResponse processPayment(PaymentRequest request) {
        return processPaymentUseCase.processPayment(request);
    }

    // 결제 내역 조회
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        return processPaymentUseCase.getPaymentsByUserId(userId);
    }
}

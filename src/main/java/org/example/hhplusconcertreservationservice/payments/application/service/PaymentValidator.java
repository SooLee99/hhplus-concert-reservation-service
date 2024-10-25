package org.example.hhplusconcertreservationservice.payments.application.service;

import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

    @Service
    public class PaymentValidator {

        public void validateAmount(BigDecimal amount) {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("유효한 결제 금액이 아닙니다.");
            }
        }

        public void validatePaymentRequest(PaymentRequest request) {
            if (request == null) {
                throw new IllegalArgumentException("결제 요청이 null입니다.");
            }

            if (request.getSeatId() == null || request.getToken() == null) {
                throw new IllegalArgumentException("결제 요청에 필요한 정보가 부족합니다.");
            }
        }
    }
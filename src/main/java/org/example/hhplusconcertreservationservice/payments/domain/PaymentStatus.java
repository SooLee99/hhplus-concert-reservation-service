package org.example.hhplusconcertreservationservice.payments.domain;

public enum PaymentStatus {
    COMPLETED,  // 결제 완료
    FAILED,     // 결제 실패
    PENDING     // 결제 대기
}
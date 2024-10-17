package org.example.hhplusconcertreservationservice.payments.domain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String description;
}


package org.example.hhplusconcertreservationservice.reservations.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("예약 대기 중"),
    CONFIRMED("예약 확정"),
    CANCELLED("예약 취소"),
    EXPIRED("만료됨");

    private final String description;
}

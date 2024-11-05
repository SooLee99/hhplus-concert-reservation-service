package org.example.hhplusconcertreservationservice.reservations.domain.entity;

public enum SeatStatus {
    AVAILABLE,              // 좌석이 예약 가능한 상태
    RESERVED,               // 좌석이 예약된 상태
    TEMPORARILY_RESERVED    // 좌석이 일시적으로 예약된 상태
}
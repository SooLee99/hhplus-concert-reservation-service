package org.example.hhplusconcertreservationservice.reservations.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationErrorMessages {
    SEAT_ALREADY_RESERVED("해당 좌석은 이미 예약된 좌석입니다."),
    MAX_SEATS_EXCEEDED("유저당 최대 예약 가능한 좌석 수를 초과했습니다."),
    RESERVATION_TIME_EXCEEDED("예약 가능한 시간이 지났습니다."),
    INVALID_RESERVATION_TIME("예약 가능한 시간이 아닙니다."),
    INVALID_SEAT("유효하지 않은 좌석입니다."),
    USER_CANNOT_RESERVE("사용자가 예약할 수 없는 상태입니다.");

    private final String message;
}

package org.example.hhplusconcertreservationservice.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessage {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    ACTIVE_TOKEN_EXISTS("이미 활성화된 토큰이 존재합니다."),
    TOKEN_EXPIRED("토큰이 만료되었습니다."),
    QUEUE_ENTRY_NOT_FOUND("대기열 정보를 찾을 수 없습니다."),
    QUEUE_CREATION_FAILED("큐 생성에 실패하였습니다."),
    REQUIRED_SERVICE_NOT_INITIALIZED("서버에 에러가 발생되었습니다."),
    SERVER_OVERLOADED("서버가 혼잡하여 요청을 처리할 수 없습니다."),
    SEAT_ALREADY_RESERVED("해당 좌석은 이미 예약된 좌석입니다."),
    MAX_SEATS_EXCEEDED("유저당 최대 예약 가능한 좌석 수를 초과했습니다."),
    RESERVATION_TIME_EXCEEDED("예약 가능한 시간이 지났습니다."),
    INVALID_RESERVATION_TIME("예약 가능한 시간이 아닙니다."),
    INVALID_SEAT("유효하지 않은 좌석입니다."),
    SEAT_NOT_FOUND("좌석을 찾을 수 없습니다."),
    CONCERT_NOT_FOUND("공연 정보를 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND("해당 날짜에 대한 공연을 찾을 수 없습니다."),
    MISSING_REQUIRED_FIELDS("필수 입력 값이 누락되었습니다."),
    SEAT_TYPE_NOT_FOUND("좌석 타입을 찾을 수 없습니다."),
    INVALID_CHARGE_AMOUNT("충전 금액은 0보다 커야 합니다."),
    INVALID_USE_AMOUNT("사용할 금액은 0보다 커야 합니다."),
    INSUFFICIENT_BALANCE("잔액이 부족합니다."),
    MISSING_TOKEN("토큰이 누락되었습니다."),
    USER_CANNOT_RESERVE("사용자가 예약할 수 없는 상태입니다.");

    private final String message;
}

package org.example.hhplusconcertreservationservice.users.application.exception;

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
    REQUIRED_SERVICE_NOT_INITIALIZED("서버에 에러가 발생되었습니다.");

    private final String message;
}

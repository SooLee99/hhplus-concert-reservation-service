package org.example.hhplusconcertreservationservice.users.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueStatus {
    TOKEN_ISSUED("토큰 발급 완료"),
    WAITING("대기열 진입"),
    PROCESSING("입장 처리 중"),
    FINISHED("입장 완료"),
    REMOVED("대기열 삭제"),
    REJECTED("입장 거부");

    private final String description;
}

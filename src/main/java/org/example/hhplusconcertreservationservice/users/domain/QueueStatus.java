package org.example.hhplusconcertreservationservice.users.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueStatus {
    TOKEN_ISSUED("토큰 발급 완료", 1),
    WAITING("대기열 진입", 2),
    PROCESSING("입장 처리 중", 3),
    FINISHED("입장 완료", 4),
    REMOVED("대기열 삭제", 5),
    REJECTED("입장 거부", 6);

    private final String description;
    private final int code;

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static QueueStatus fromCode(int code) {
        for (QueueStatus status : QueueStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

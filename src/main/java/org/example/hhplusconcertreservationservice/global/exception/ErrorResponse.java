package org.example.hhplusconcertreservationservice.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;         // HTTP 상태 코드
    private String errorCode;   // 예외 이넘 코드 (ExceptionMessage의 이름)
    private String message;     // 예외 메시지 (ExceptionMessage에서 제공하는 메시지)
}

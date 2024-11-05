package org.example.hhplusconcertreservationservice.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException 예외 처리 메서드
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 예외 메시지로 ExceptionMessage에서 적절한 값을 찾아 설정
        ExceptionMessage exceptionMessage = getExceptionMessage(ex.getMessage());

        // ErrorResponse에 에러 코드 및 메시지 반환
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exceptionMessage.name(),  // 이넘 이름을 코드로 사용
                exceptionMessage.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * IllegalArgumentException의 메시지로 ExceptionMessage 이넘 값을 가져오는 메서드
     */
    private ExceptionMessage getExceptionMessage(String message) {
        for (ExceptionMessage exceptionMessage : ExceptionMessage.values()) {
            if (exceptionMessage.getMessage().equals(message)) {
                return exceptionMessage;
            }
        }
        // 메시지가 매칭되지 않으면 기본 예외 메시지 반환
        return ExceptionMessage.REQUIRED_SERVICE_NOT_INITIALIZED;
    }
}

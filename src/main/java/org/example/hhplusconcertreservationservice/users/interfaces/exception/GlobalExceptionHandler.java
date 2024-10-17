package org.example.hhplusconcertreservationservice.users.interfaces.exception;

import lombok.Getter;
import org.example.hhplusconcertreservationservice.users.application.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ActiveTokenExistsException.class)
    public ResponseEntity<ErrorResponse> handleActiveTokenExistsException(ActiveTokenExistsException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(QueueEntryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleQueueEntryNotFoundException(QueueEntryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(QueueCreationFailedException.class)
    public ResponseEntity<ErrorResponse> handleQueueCreationFailedException(QueueCreationFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getMessage()));
    }

    // 기타 예외 처리 추가

    // 에러 응답 DTO
    @Getter
    static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}

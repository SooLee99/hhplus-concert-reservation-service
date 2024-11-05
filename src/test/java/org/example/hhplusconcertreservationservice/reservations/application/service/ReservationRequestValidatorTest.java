package org.example.hhplusconcertreservationservice.reservations.application.service;

import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationRequestValidatorTest {

    private ReservationRequestValidator reservationRequestValidator = new ReservationRequestValidator();

    @Test
    @DisplayName("유효한 요청이면 예외가 발생하지 않아야 한다")
    void validate_ShouldNotThrowException_WhenRequestIsValid() {
        // Given
        CreateReservationRequest request = new CreateReservationRequest("valid_token", 100L);

        // When & Then
        assertDoesNotThrow(() -> reservationRequestValidator.validate(request));
    }

    @Test
    @DisplayName("토큰이 null이면 IllegalArgumentException이 발생해야 한다")
    void validate_ShouldThrowException_WhenTokenIsNull() {
        // Given
        CreateReservationRequest request = new CreateReservationRequest(null, 100L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reservationRequestValidator.validate(request)
        );
        assertEquals("필수 입력 값이 누락되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("좌석 ID가 null이면 IllegalArgumentException이 발생해야 한다")
    void validate_ShouldThrowException_WhenSeatIdIsNull() {
        // Given
        CreateReservationRequest request = new CreateReservationRequest("valid_token", null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reservationRequestValidator.validate(request)
        );
        assertEquals("필수 입력 값이 누락되었습니다.", exception.getMessage());
    }
}

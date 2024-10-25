package org.example.hhplusconcertreservationservice.payments.application.service;

import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentValidatorTest {

    private PaymentValidator paymentValidator = new PaymentValidator();

    @Test
    @DisplayName("결제 금액이 유효하면 예외가 발생하지 않아야 한다")
    void validateAmount_ShouldNotThrowException_WhenAmountIsValid() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(100.0);

        // When & Then
        assertDoesNotThrow(() -> paymentValidator.validateAmount(amount));
    }

    @Test
    @DisplayName("결제 금액이 null이거나 0 이하이면 예외가 발생해야 한다")
    void validateAmount_ShouldThrowException_WhenAmountIsInvalid() {
        // Given
        BigDecimal amount = BigDecimal.ZERO;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentValidator.validateAmount(amount));
        assertEquals("유효한 결제 금액이 아닙니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 요청이 유효하면 예외가 발생하지 않아야 한다")
    void validatePaymentRequest_ShouldNotThrowException_WhenRequestIsValid() {
        // Given
        PaymentRequest request = new PaymentRequest(1L, "valid_token");

        // When & Then
        assertDoesNotThrow(() -> paymentValidator.validatePaymentRequest(request));
    }

    @Test
    @DisplayName("결제 요청이 null이면 예외가 발생해야 한다")
    void validatePaymentRequest_ShouldThrowException_WhenRequestIsNull() {
        // Given
        PaymentRequest request = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentValidator.validatePaymentRequest(request));
        assertEquals("결제 요청이 null입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 요청에 필요한 정보가 부족하면 예외가 발생해야 한다")
    void validatePaymentRequest_ShouldThrowException_WhenRequestHasMissingInfo() {
        // Given
        PaymentRequest request = new PaymentRequest(null, null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentValidator.validatePaymentRequest(request));
        assertEquals("결제 요청에 필요한 정보가 부족합니다.", exception.getMessage());
    }
}

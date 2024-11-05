package org.example.hhplusconcertreservationservice.users.application.service.balance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceValidationServiceTest {

    private BalanceValidationService balanceValidationService;

    @BeforeEach
    void setUp() {
        balanceValidationService = new BalanceValidationService();
    }

    @Test
    @DisplayName("충전 금액이 null이면 IllegalArgumentException이 발생한다")
    void validateChargeAmount_ShouldThrowException_WhenAmountIsNull() {
        // Given
        BigDecimal amount = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceValidationService.validateChargeAmount(amount)
        );
        assertEquals("충전 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("충전 금액이 0이면 IllegalArgumentException이 발생한다")
    void validateChargeAmount_ShouldThrowException_WhenAmountIsZero() {
        // Given
        BigDecimal amount = BigDecimal.ZERO;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceValidationService.validateChargeAmount(amount)
        );
        assertEquals("충전 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("충전 금액이 음수이면 IllegalArgumentException이 발생한다")
    void validateChargeAmount_ShouldThrowException_WhenAmountIsNegative() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(-1000);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceValidationService.validateChargeAmount(amount)
        );
        assertEquals("충전 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("충전 금액이 양수이면 예외가 발생하지 않는다")
    void validateChargeAmount_ShouldNotThrowException_WhenAmountIsPositive() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(1000);

        // When & Then
        assertDoesNotThrow(() ->
                balanceValidationService.validateChargeAmount(amount)
        );
    }

    @Test
    @DisplayName("사용 금액이 null이면 IllegalArgumentException이 발생한다")
    void validateUseAmount_ShouldThrowException_WhenAmountIsNull() {
        // Given
        BigDecimal amount = null;
        BigDecimal currentBalance = BigDecimal.valueOf(1000);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceValidationService.validateUseAmount(amount, currentBalance)
        );
        assertEquals("사용할 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용 금액이 0이면 IllegalArgumentException이 발생한다")
    void validateUseAmount_ShouldThrowException_WhenAmountIsZero() {
        // Given
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal currentBalance = BigDecimal.valueOf(1000);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceValidationService.validateUseAmount(amount, currentBalance)
        );
        assertEquals("사용할 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용 금액이 음수이면 IllegalArgumentException이 발생한다")
    void validateUseAmount_ShouldThrowException_WhenAmountIsNegative() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(-500);
        BigDecimal currentBalance = BigDecimal.valueOf(1000);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceValidationService.validateUseAmount(amount, currentBalance)
        );
        assertEquals("사용할 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("현재 잔액보다 사용 금액이 크면 IllegalStateException이 발생한다")
    void validateUseAmount_ShouldThrowException_WhenAmountExceedsBalance() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(1500);
        BigDecimal currentBalance = BigDecimal.valueOf(1000);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                balanceValidationService.validateUseAmount(amount, currentBalance)
        );
        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용 금액이 현재 잔액 이하이면 예외가 발생하지 않는다")
    void validateUseAmount_ShouldNotThrowException_WhenAmountIsWithinBalance() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        BigDecimal currentBalance = BigDecimal.valueOf(1000);

        // When & Then
        assertDoesNotThrow(() ->
                balanceValidationService.validateUseAmount(amount, currentBalance)
        );
    }
}

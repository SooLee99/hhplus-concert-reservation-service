package org.example.hhplusconcertreservationservice.users.application.service.balance;

import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class BalanceValidationService {

    /**
     * 충전 금액이 유효한지 확인합니다.
     * @param amount 충전할 금액
     */
    public void validateChargeAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_CHARGE_AMOUNT.getMessage());
        }
    }

    /**
     * 잔액 사용(차감) 시 금액이 유효한지 확인합니다.
     * @param amount 사용하려는 금액
     * @param currentBalance 현재 잔액
     */
    public void validateUseAmount(BigDecimal amount, BigDecimal currentBalance) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_USE_AMOUNT.getMessage());
        }
        if (currentBalance.compareTo(amount) < 0) {
            throw new IllegalStateException(ExceptionMessage.INSUFFICIENT_BALANCE.getMessage());
        }
    }
}

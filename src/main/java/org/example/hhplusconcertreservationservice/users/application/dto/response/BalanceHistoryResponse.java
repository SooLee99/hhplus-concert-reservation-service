package org.example.hhplusconcertreservationservice.users.application.dto.response;

import lombok.Getter;
import org.example.hhplusconcertreservationservice.users.domain.UserBalanceHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BalanceHistoryResponse {

    private final BigDecimal amount;
    private final BigDecimal balanceAfterTransaction;
    private final String transactionType;
    private final LocalDateTime createdAt;

    public BalanceHistoryResponse(UserBalanceHistory history) {
        this.amount = history.getAmount();
        this.balanceAfterTransaction = history.getBalanceAfterTransaction();
        this.transactionType = history.getTransactionType().name();
        this.createdAt = history.getCreatedAt();
    }
}

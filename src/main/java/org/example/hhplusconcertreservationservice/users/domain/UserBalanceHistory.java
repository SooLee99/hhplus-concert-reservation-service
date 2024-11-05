package org.example.hhplusconcertreservationservice.users.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_balance_histories")
public class UserBalanceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfterTransaction;

    @Column(name = "transaction_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // 충전 or 감소

    @Builder
    public UserBalanceHistory(Long userId, BigDecimal amount, BigDecimal balanceAfterTransaction, TransactionType transactionType) {
        this.userId = userId;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.transactionType = transactionType;
    }
}

package org.example.hhplusconcertreservationservice.users.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_balances", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
})
public class UserBalance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Builder
    public UserBalance(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    /**
     * 잔액 업데이트 메서드
     * @param updatedBalance 새 잔액
     */
    public void updateBalance(BigDecimal updatedBalance) {
        this.balance = updatedBalance;
    }

    /**
     * 잔액 충전 메서드
     * @param amount 충전할 금액
     */
    public void charge(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    /**
     * 잔액 사용(차감) 메서드
     * @param amount 사용(차감)할 금액
     */
    public void use(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
}

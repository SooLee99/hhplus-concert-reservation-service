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
@Table(name = "user_balances")
/// UserBalance 엔티티: 사용자의 잔액 정보를 저장하는 엔티티
public class UserBalance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Builder
    public UserBalance(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }
}

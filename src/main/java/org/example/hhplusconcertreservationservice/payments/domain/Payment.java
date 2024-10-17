package org.example.hhplusconcertreservationservice.payments.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
/// Payment 엔티티: 결제 정보를 저장하는 엔티티
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_status",nullable = false, length = 50)
    private String paymentStatus;

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @PrePersist
    protected void onCreate() {
        this.paymentTime = LocalDateTime.now();
    }

    @Builder
    public Payment(BigDecimal amount, String paymentStatus) {
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }
}

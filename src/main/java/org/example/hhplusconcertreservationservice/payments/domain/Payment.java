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
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 50)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @PrePersist
    protected void onCreate() {
        this.paymentTime = LocalDateTime.now();
    }

    @Builder
    public Payment(Long paymentId, Long userId, BigDecimal amount, PaymentStatus paymentStatus) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}

package org.example.hhplusconcertreservationservice.reservations.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name ="user_id", nullable = false)
    private Long userId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false, length = 50)
    private ReservationStatus reservationStatus;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;  // 예약 시간 필드

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;   // 만료 시간 필드

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;
    @PrePersist
    protected void onCreate() {
        this.reservationTime = LocalDateTime.now();  // 기본적으로 예약 시간 설정
    }

    @Builder
    public Reservation(Long userId, Long paymentId, Long seatId, Long scheduleId,
                       ReservationStatus reservationStatus, LocalDateTime reservationTime,
                       LocalDateTime expirationTime) {
        this.userId = userId;
        this.paymentId = paymentId;
        this.seatId = seatId;
        this.scheduleId = scheduleId;
        this.reservationStatus = reservationStatus;
        this.reservationTime = reservationTime;
        this.expirationTime = expirationTime;
    }

    public void expire() {
        this.reservationStatus = ReservationStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }
}


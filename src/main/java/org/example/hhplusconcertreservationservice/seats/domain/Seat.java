package org.example.hhplusconcertreservationservice.seats.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seats")
/// Seat 엔티티: 좌석 정보를 저장하는 엔티티
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "seat_type_id", nullable = false)
    private Long seatTypeId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(name = "is_reserved", nullable = false)
    private boolean isReserved;

    @Builder
    public Seat(Long seatId, Long seatTypeId, Long scheduleId, int seatNumber, boolean isReserved) {
        this.seatId = seatId; // seatId를 수동으로 설정
        this.seatTypeId = seatTypeId;
        this.scheduleId = scheduleId;
        this.seatNumber = seatNumber;
        this.isReserved = isReserved;
    }


    // 좌석 유효성 검증 메서드
    public boolean isValid() {
        // 좌석 번호가 0보다 크고, 좌석이 아직 예약되지 않은 경우 유효하다고 판단
        return seatNumber > 0;
    }
    // 좌석 예약 여부 반환
    public boolean isReserved() {
        return isReserved;
    }

    // 좌석 예약 상태 설정
    public void reserve() {
        this.isReserved = true;
    }

    // 좌석 예약 취소 상태 설정
    public void cancelReservation() {
        this.isReserved = false;
    }
}


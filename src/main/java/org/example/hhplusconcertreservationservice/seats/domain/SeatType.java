package org.example.hhplusconcertreservationservice.seats.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat_types")
/// SeatType 엔티티: 좌석 유형 정보를 저장하는 엔티티
public class SeatType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_type_id")
    private Long seatTypeId;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "seat_type_name", length = 100)
    private String seatTypeName;

    @Builder
    public SeatType(Long seatTypeId, Long concertId, BigDecimal price, String seatTypeName) {
        this.seatTypeId = seatTypeId;
        this.concertId = concertId;
        this.price = price;
        this.seatTypeName = seatTypeName;
    }
}

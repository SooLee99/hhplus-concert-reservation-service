package org.example.hhplusconcertreservationservice.seats.infrastructure;

import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Long> {
    // 공연 ID로 좌석 유형 조회
    List<SeatType> findByConcertId(Long concertId);
}
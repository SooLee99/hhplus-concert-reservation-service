package org.example.hhplusconcertreservationservice.seats.infrastructure;

import jakarta.persistence.LockModeType;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    // 일정 ID로 좌석 조회
    List<Seat> findByScheduleId(Long scheduleId);

    // 일정 ID와 좌석 번호로 좌석 조회
    Optional<Seat> findByScheduleIdAndSeatNumber(Long scheduleId, int seatNumber);

    // 동시성 문제 방지를 위한 비관적 락 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findByIdForUpdate(@Param("seatId") Long seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Seat findBySeatId(Long seatId);

    Optional<Seat> findBySeatIdAndIsReservedFalse(Long seatId);
}
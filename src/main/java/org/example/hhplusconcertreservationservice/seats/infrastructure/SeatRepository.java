package org.example.hhplusconcertreservationservice.seats.infrastructure;

import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByScheduleId(Long scheduleId);

    // 좌석 예약 상태 확인
    @Query("SELECT s.isReserved FROM Seat s WHERE s.seatId = :seatId")
    boolean isReserved(@Param("seatId") Long seatId);

    // 좌석 예약 상태 변경
    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.isReserved = :isReserved WHERE s.seatId = :seatId")
    void setIsReserved(@Param("seatId") Long seatId, @Param("isReserved") boolean isReserved);

    Optional<Seat> findBySeatId(Long seatId);
}
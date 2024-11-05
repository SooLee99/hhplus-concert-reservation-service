package org.example.hhplusconcertreservationservice.reservations.infrastructure;

import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 날짜의 예약된 좌석 ID 목록 조회
    @Query("SELECT r.seatId FROM Reservation r WHERE r.reservationDate = :date")
    List<Long> findReservedSeatIdsByDateTime(@Param("date") LocalDateTime date);

    // 특정 상태의 예약 중 만료 시간이 지난 예약들을 조회
    List<Reservation> findByReservationStatusAndExpirationTimeBefore(ReservationStatus status, LocalDateTime time);

    // 스케줄 ID로 예약된 좌석 ID 조회
    @Query("SELECT r.seatId FROM Reservation r WHERE r.scheduleId = :scheduleId")
    List<Long> findReservedSeatIdsByScheduleId(@Param("scheduleId") Long scheduleId);

    Optional<Reservation> findBySeatIdAndUserId(Long seatId, Long userId);
}

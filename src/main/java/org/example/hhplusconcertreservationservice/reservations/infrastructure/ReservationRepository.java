package org.example.hhplusconcertreservationservice.reservations.infrastructure;

import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r.seatId FROM Reservation r WHERE r.reservationTime = :date AND r.reservationStatus = 'CONFIRMED'")
    List<Long> findReservedSeatIdsByDate(LocalDateTime date);

    // 특정 상태의 예약 중 만료 시간이 지난 예약들을 조회
    List<Reservation> findByReservationStatusAndExpirationTimeBefore(ReservationStatus status, LocalDateTime time);
}

package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationExpirationScheduler {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void expireReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findByReservationStatusAndExpirationTimeBefore(ReservationStatus.PENDING, now);

        for (Reservation reservation : expiredReservations) {
            // 예약 상태를 EXPIRED로 변경
            reservation.expire();
            reservationRepository.save(reservation);

            // 좌석의 예약 상태를 해제
            Seat seat = seatRepository.findById(reservation.getSeatId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
            seat.cancelReservation();
            seatRepository.save(seat);
        }
    }
}
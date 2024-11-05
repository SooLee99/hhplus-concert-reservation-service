package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
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
            reservation.expire();
            reservationRepository.save(reservation);
            seatRepository.setIsReserved(reservation.getSeatId(), false);
            // 좌석의 예약 상태를 해제
            Seat seat = seatRepository.findById(reservation.getSeatId())
                    .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.SEAT_NOT_FOUND.getMessage()));
            seat.cancelReservation();
            seatRepository.save(seat);
        }
    }
}
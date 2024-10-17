package org.example.hhplusconcertreservationservice.reservations.domain;

import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.exception.InvalidReservationException;
import org.example.hhplusconcertreservationservice.reservations.domain.exception.ReservationErrorMessages;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 예약 요청에 대한 유효성을 검사하는 클래스
 */
@Component
public class ReservationValidator {

    /**
     * 좌석 예약 요청의 유효성을 검사하는 메서드
     *
     * @param seat         좌석 정보
     * @param reservation  예약 정보
     * @throws InvalidReservationException  예약 요청이 유효하지 않은 경우 발생
     */
    public void validate(Seat seat, Reservation reservation) {
        // 1. 좌석이 이미 예약된 상태인지 확인
        if (seat.isReserved()) {
            throw new InvalidReservationException(ReservationErrorMessages.SEAT_ALREADY_RESERVED);
        }

        // 2. 예약 가능한 시간대인지 확인 (예: 예약이 1시간 이내인지 등 정책에 따라 변경 가능)
        if (!isValidReservationTime(reservation.getReservationTime())) {
            throw new InvalidReservationException(ReservationErrorMessages.INVALID_RESERVATION_TIME);
        }

        // 3. 예약하려는 좌석이 유효한 좌석인지 확인 (좌석 번호 또는 상태 기반으로 검증)
        if (!seat.isValid()) {
            throw new InvalidReservationException(ReservationErrorMessages.INVALID_SEAT);
        }

        /* 4. 사용자나 다른 예약 조건이 유효한지 추가 검증
        if (reservation.getUserId() == null) {
            throw new InvalidReservationException(ReservationErrorMessages.USER_CANNOT_RESERVE);
        }*/
    }

    /**
     * 예약 시간의 유효성을 검사하는 메서드
     *
     * @param reservationTime 예약하려는 시간
     * @return 예약 가능한 시간인지 여부
     */
    private boolean isValidReservationTime(LocalDateTime reservationTime) {
        // 현재 시간보다 예약 시간이 미래에 있어야 유효
        return reservationTime.isAfter(LocalDateTime.now());
    }
}

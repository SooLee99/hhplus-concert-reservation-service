package org.example.hhplusconcertreservationservice.reservations.domain;

import org.example.hhplusconcertreservationservice.reservations.domain.exception.ReservationErrorMessages;
import org.example.hhplusconcertreservationservice.reservations.domain.exception.ReservationNotAllowedException;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.users.domain.User;

import java.time.LocalDateTime;

/**
 * ReservationPolicy 클래스는 예약이 가능한지 여부를 결정하는 비즈니스 규칙을 관리합니다.
 * 예약 정책에 따라 좌석 예약 가능 여부를 확인합니다.
 */
public class ReservationPolicy {

    private final int maxSeatsPerUser; // 유저당 최대 예약 가능 좌석 수
    private final int reservationTimeLimitMinutes; // 예약 가능 시간 (분)
    private final LocalDateTime concertStartTime; // 콘서트 시작 시간

    public ReservationPolicy(int maxSeatsPerUser, int reservationTimeLimitMinutes, LocalDateTime concertStartTime) {
        this.maxSeatsPerUser = maxSeatsPerUser;
        this.reservationTimeLimitMinutes = reservationTimeLimitMinutes;
        this.concertStartTime = concertStartTime;
    }

    /**
     * 유저가 특정 좌석을 예약할 수 있는지 확인합니다.
     * - 유저의 예약한 좌석 수가 최대 예약 가능 수를 초과하지 않았는지 확인
     * - 좌석이 이미 예약되어 있지 않은지 확인
     * - 예약 요청 시간이 콘서트 시작 시간 전에 이루어졌는지 확인
     *
     * @param user 예약을 시도하는 사용자
     * @param seat 예약하려는 좌석
     * @param currentReservations 현재 유저의 예약 개수
     * @throws ReservationNotAllowedException 예약이 허용되지 않을 경우 발생
     */
    public void validateReservation(User user, Seat seat, int currentReservations) throws ReservationNotAllowedException {
        checkSeatAvailability(seat);
        checkMaxSeatsPerUser(currentReservations);
        checkReservationTimeLimit();
    }

    /**
     * 좌석이 이미 예약된 상태인지 확인합니다.
     * @param seat 예약하려는 좌석
     * @throws ReservationNotAllowedException 좌석이 이미 예약된 경우
     */
    private void checkSeatAvailability(Seat seat) {
        if (seat.isReserved()) {
            throw new ReservationNotAllowedException(ReservationErrorMessages.SEAT_ALREADY_RESERVED.getMessage());
        }
    }

    /**
     * 유저가 최대 예약 가능한 좌석 수를 초과하지 않았는지 확인합니다.
     * @param currentReservations 현재 유저가 예약한 좌석 수
     * @throws ReservationNotAllowedException 유저가 최대 예약 좌석 수를 초과한 경우
     */
    private void checkMaxSeatsPerUser(int currentReservations) {
        if (currentReservations >= maxSeatsPerUser) {
            throw new ReservationNotAllowedException(ReservationErrorMessages.MAX_SEATS_EXCEEDED.getMessage());
        }
    }

    /**
     * 예약 가능 시간이 지났는지 확인합니다.
     * 콘서트 시작 시간으로부터 일정 시간이 지나면 예약이 불가능하게 설정
     * @throws ReservationNotAllowedException 예약 시간이 지나 예약할 수 없는 경우
     */
    private void checkReservationTimeLimit() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(concertStartTime.minusMinutes(reservationTimeLimitMinutes))) {
            throw new ReservationNotAllowedException(ReservationErrorMessages.RESERVATION_TIME_EXCEEDED.getMessage());
        }
    }
}

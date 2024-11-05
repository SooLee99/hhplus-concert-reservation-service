package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SeatOwnershipService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final QueueManager queueManager;

    /**
     * 좌석 소유권을 사용자에게 배정하고 예약 상태를 '확정'으로 변경하는 메서드
     *
     * @param userId    사용자 ID
     * @param seatId    좌석 번호
     */
    @Transactional
    public void assignSeatToUser(Long userId, Long seatId) {
        // 1. 예약 정보 조회
        Reservation reservation = findReservationForSeat(userId, seatId);

        // 2. 좌석 소유권 배정
        reserveSeat(seatId);

        // 3. 예약 상태를 '확정'으로 업데이트
        updateReservationStatusToConfirmed(reservation);

        // 5. 대기열에서 사용자 제거
        queueManager.removeUserFromQueue(userId);
    }

    /**
     * 좌석을 예약된 상태로 변경
     *
     * @param seatId 좌석 ID
     */
    private void reserveSeat(Long seatId) {
        seatRepository.setIsReserved(seatId, true); // 좌석을 예약 상태로 설정
    }

    /**
     * 예약 상태를 'CONFIRMED'로 업데이트
     *
     * @param reservation 예약 객체
     */
    private void updateReservationStatusToConfirmed(Reservation reservation) {
        reservation.updateStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation); // 변경된 상태 저장
    }

    /**
     * 예약 정보를 조회하는 메서드
     *
     * @param userId  사용자 ID
     * @param seatId  좌석 ID
     * @return Reservation 예약 정보
     */
    public Reservation findReservationForSeat(Long userId, Long seatId) {
        return reservationRepository.findBySeatIdAndUserId(seatId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석에 대한 예약 정보가 없습니다."));
    }
}

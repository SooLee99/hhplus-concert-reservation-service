package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.CreateReservationUseCase;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.CreateReservationResponse;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateReservationService implements CreateReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final ReservationRequestValidator reservationRequestValidator;
    private final QueueService queueService;

    @Transactional
    @Override
    public CreateReservationResponse createReservation(CreateReservationRequest request) {
        // 1. 대기열 토큰을 검증하여 유저가 대기열에 포함되어 있는지 확인
        Long userId = queueService.getUserIdFromToken(request.getToken());

        // 2. 요청 유효성 검증
        reservationRequestValidator.validate(request);

        // 3. 좌석이 존재하고 예약 가능한지 확인
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.INVALID_SEAT.getMessage()));

        if (seat.isReserved()) {
            throw new IllegalArgumentException(ExceptionMessage.SEAT_ALREADY_RESERVED.getMessage());
        }


        // 4. 좌석 예약 처리 (비관적 락 사용)
        seat.reserve();
        seatRepository.save(seat); // 좌석 상태 업데이트

        try {
            // 5. 좌석의 scheduleId를 사용하여 예약 엔티티 생성
            Reservation reservation = Reservation.builder()
                    .userId(userId)
                    .seatId(request.getSeatId())
                    .scheduleId(seat.getScheduleId())
                    .reservationStatus(ReservationStatus.PENDING)
                    .reservationTime(LocalDateTime.now())
                    .expirationTime(LocalDateTime.now().plusMinutes(5))
                    .build();

            // 6. 예약 정보 저장
            Reservation savedReservation = reservationRepository.save(reservation);

            // 7. 응답 DTO 생성 및 반환
            return new CreateReservationResponse(savedReservation);
        } catch (Exception e) {
            // 예약 생성 중 오류 발생 시 좌석 예약 취소
            seat.cancelReservation();
            seatRepository.save(seat);
            throw new IllegalArgumentException(ExceptionMessage.QUEUE_CREATION_FAILED.getMessage(), e);
        }
    }
}

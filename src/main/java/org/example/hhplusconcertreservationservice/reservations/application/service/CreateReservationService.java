package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.application.exception.ReservationCreationException;
import org.example.hhplusconcertreservationservice.reservations.application.exception.SeatNotAvailableException;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.CreateReservationResponse;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final applicationReservationValidator applicationReservationValidator;

    @Transactional
    public CreateReservationResponse createReservation(CreateReservationRequest request) {
        // 1. 요청 유효성 검증
        applicationReservationValidator.validate(request);

        // 2. 좌석이 존재하고 예약 가능한지 확인
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new SeatNotAvailableException("해당 좌석을 찾을 수 없습니다."));

        if (seat.isReserved()) {
            throw new SeatNotAvailableException("해당 좌석은 이미 예약되었습니다.");
        }

        // 3. 좌석 예약 처리 (비관적 락 사용)
        seat.reserve();
        seatRepository.save(seat); // 좌석 상태 업데이트

        try {
            // 4. 예약 엔티티 생성
            Reservation reservation = Reservation.builder()
                    .userId(request.getUserId())
                    .seatId(request.getSeatId())
                    .scheduleId(request.getScheduleId())
                    .reservationStatus(ReservationStatus.PENDING)
                    .reservationStatus(ReservationStatus.valueOf(String.valueOf(LocalDateTime.now())))
                    .expirationTime(LocalDateTime.now().plusMinutes(5)) // 임시 예약 만료 시간 설정
                    .build();

            // 5. 예약 정보 저장
            Reservation savedReservation = reservationRepository.save(reservation);

            // 6. 응답 DTO 생성 및 반환
            return new CreateReservationResponse(savedReservation);
        } catch (Exception e) {
            // 예약 생성 중 오류 발생 시 좌석 예약 취소
            seat.cancelReservation();
            seatRepository.save(seat);
            throw new ReservationCreationException("예약 생성 중 오류가 발생했습니다.", e);
        }
    }
}
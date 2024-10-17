package org.example.hhplusconcertreservationservice.reservations.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.CreateReservationResponse;
import org.example.hhplusconcertreservationservice.reservations.application.exception.SeatNotAvailableException;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public CreateReservationResponse createReservation(CreateReservationRequest request) {
        // 1. 좌석이 존재하고 예약 가능한지 확인
        Seat seat = seatRepository.findBySeatIdAndIsReservedFalse(request.getSeatId())
                .orElseThrow(() -> new SeatNotAvailableException("해당 좌석은 예약이 불가능합니다."));

        // 2. 좌석 예약 처리
        seat.reserve();
        seatRepository.save(seat); // 좌석 상태 업데이트

        // 3. 예약 엔티티 생성
        Reservation reservation = Reservation.builder()
                .userId(request.getUserId())
                .seatId(request.getSeatId())
                .scheduleId(request.getScheduleId())
                .reservationStatus(ReservationStatus.PENDING)
                .reservationTime(LocalDateTime.now())  // 예약 시간 설정
                .expirationTime(LocalDateTime.now().plusMinutes(5))  // 임시 예약 만료 시간 설정
                .build();

        // 4. 예약 정보 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. 응답 DTO 생성 및 반환
        return new CreateReservationResponse(savedReservation);
    }
}
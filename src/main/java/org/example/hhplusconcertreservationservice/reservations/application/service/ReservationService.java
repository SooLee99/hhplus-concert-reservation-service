package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.example.hhplusconcertreservationservice.reservations.application.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.CreateReservationResponse;
import org.example.hhplusconcertreservationservice.reservations.application.exception.SeatNotAvailableException;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 가능한 날짜 목록을 조회합니다.
     *
     * @return 예약 가능한 날짜 리스트
     */
    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableDates() {
        List<ConcertSchedule> schedules = concertScheduleRepository.findAll();
        return schedules.stream()
                .map(ConcertSchedule::getDate)
                .collect(Collectors.toList());
    }

    /**
     * 특정 날짜의 좌석 정보(예약 상태)를 조회합니다.
     *
     * @param date 조회할 날짜
     * @return 좌석 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<SeatInfoResponse> getSeatInfoByDate(LocalDateTime date) {
        // 모든 좌석 목록 조회
        List<Seat> allSeats = seatRepository.findAll();

        // 해당 날짜의 예약된 좌석 ID 목록 조회
        List<Long> reservedSeatIds = reservationRepository.findReservedSeatIdsByDate(date);

        // 좌석 상태를 설정하여 응답 생성
        return allSeats.stream()
                .map(seat -> {
                    SeatStatus status = reservedSeatIds.contains(seat.getSeatId()) ? SeatStatus.RESERVED : SeatStatus.AVAILABLE;
                    return new SeatInfoResponse(seat.getSeatId(), status); // seat.getSeatId()가 null이 아닌지 확인
                })
                .collect(Collectors.toList());
    }

    /**
     * 좌석 예약을 생성합니다.
     *
     * @param request 예약 요청 정보
     * @return 생성된 예약 정보 응답
     */
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

    /**
     * 예약 만료를 처리하는 스케줄러 메서드입니다.
     * 1분마다 실행되어 만료된 예약을 찾아 상태를 변경하고 좌석을 해제합니다.
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void expirePendingReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findByReservationStatusAndExpirationTimeBefore(
                ReservationStatus.PENDING, now);

        for (Reservation reservation : expiredReservations) {
            // 예약 상태를 EXPIRED로 변경
            reservation.expire();  // 예약 상태 변경
            reservationRepository.save(reservation);  // 변경된 예약 상태 저장

            // 좌석의 예약 상태를 해제
            Seat seat = seatRepository.findById(reservation.getSeatId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
            seat.cancelReservation();
            seatRepository.save(seat);
        }
    }
}


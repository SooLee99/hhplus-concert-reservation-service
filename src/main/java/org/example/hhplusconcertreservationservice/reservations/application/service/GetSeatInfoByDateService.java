package org.example.hhplusconcertreservationservice.reservations.application.service;


import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetSeatInfoByDateUseCase;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetSeatInfoByDateService implements GetSeatInfoByDateUseCase {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SeatInfoResponse> getSeatInfoByDate(LocalDateTime date) {
        // 모든 좌석 목록 조회
        List<Seat> allSeats = seatRepository.findAll();

        // 해당 날짜의 예약된 좌석 ID 목록 조회
        List<Long> reservedSeatIds = reservationRepository.findReservedSeatIdsByDate(date);

        // 좌석 상태를 설정하여 응답 생성
        return allSeats.stream()
                .map(seat -> {
                    SeatStatus status = reservedSeatIds.contains(seat.getSeatId())
                            ? SeatStatus.RESERVED
                            : SeatStatus.AVAILABLE;
                    return new SeatInfoResponse(seat.getSeatId(), status);
                })
                .collect(Collectors.toList());
    }
}
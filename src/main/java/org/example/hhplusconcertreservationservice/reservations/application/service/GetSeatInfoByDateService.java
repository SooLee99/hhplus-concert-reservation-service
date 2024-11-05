package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertImageService;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertScheduleService;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertService;
import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.ConcertInfoResponse;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetSeatInfoByDateUseCase;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;
import org.example.hhplusconcertreservationservice.seats.application.service.SeatService;
import org.example.hhplusconcertreservationservice.seats.application.service.SeatTypeService;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSeatInfoByDateService implements GetSeatInfoByDateUseCase {

    private final SeatService seatService;
    private final SeatTypeService seatTypeService;
    private final ReservationRepository reservationRepository;
    private final ConcertService concertService;
    private final ConcertScheduleService concertScheduleService;
    private final ConcertImageService concertImageService;
    private final QueueService queueService;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSeatInfoByDateTime(LocalDateTime date, String token) {
        // 토큰 검증
        queueService.validateQueueToken(token);

        // 공연 스케줄 ID를 날짜로 찾음
        Long scheduleId = concertScheduleService.getScheduleIdByDate(date)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.SCHEDULE_NOT_FOUND.getMessage()));

        // 해당 스케줄의 모든 좌석을 SeatService를 통해 조회
        List<Seat> allSeats = seatService.findAllByScheduleId(scheduleId);

        // 해당 스케줄의 예약된 좌석 ID 조회
        List<Long> reservedSeatIds = reservationRepository.findReservedSeatIdsByScheduleId(scheduleId);

        // 좌석 상태를 설정하여 좌석 정보 리스트 생성
        List<SeatInfoResponse> seatInfoList = allSeats.stream()
                .map(seat -> {
                    SeatStatus status = reservedSeatIds.contains(seat.getSeatId()) ? SeatStatus.RESERVED : SeatStatus.AVAILABLE;
                    return SeatInfoResponse.builder()
                            .seatId(seat.getSeatId())
                            .seatNumber(seat.getSeatNumber())
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());

        // 공연 스케줄 정보 조회
        ConcertSchedule concertSchedule = concertScheduleService.getConcertScheduleById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.SCHEDULE_NOT_FOUND.getMessage()));

        // 공연 정보 조회 및 응답 생성
        SeatType seatType = seatTypeService.getSeatTypeById(allSeats.get(0).getSeatTypeId())
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.SEAT_TYPE_NOT_FOUND.getMessage()));

        Concert concert = concertService.getConcertById(seatType.getConcertId())
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.CONCERT_NOT_FOUND.getMessage()));


        // 공연 이미지 리스트 조회
        List<String> imageUrls = concertImageService.getImagesByConcertId(concert.getConcertId());

        // price와 rating 처리
        double price = Optional.ofNullable(seatType.getPrice()).map(BigDecimal::doubleValue).orElse(0.0);
        double rating = Optional.ofNullable(concert.getRating()).map(Double::parseDouble).orElse(0.0);

        // 공연 스케줄 정보를 포함한 응답 DTO 생성
        ConcertInfoResponse concertInfo = ConcertInfoResponse.builder()
                .seatTypeName(seatType.getSeatTypeName())
                .price(price)
                .concertTitle(concert.getTitle())
                .posterUrl(concert.getPosterUrl())
                .location(concert.getLocation())
                .rating(rating)
                .imageUrls(imageUrls)  // 여러 이미지 처리
                .concertDate(concertSchedule.getDate())
                .duration(concertSchedule.getDuration())
                .ticketStartTime(concertSchedule.getTicketStartTime())
                .ticketEndTime(concertSchedule.getTicketEndTime())
                .build();

        // 좌석 정보와 공연 정보 함께 반환
        Map<String, Object> response = new HashMap<>();
        response.put("concertInfo", concertInfo);
        response.put("seatInfoList", seatInfoList);

        return response;
    }
}

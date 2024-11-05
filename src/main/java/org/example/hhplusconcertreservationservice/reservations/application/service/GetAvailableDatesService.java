package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertImageService;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertService;
import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.ConcertInfoResponse;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.SeatTypeInfoResponse;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetAvailableDatesUseCase;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatTypeRepository;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAvailableDatesService implements GetAvailableDatesUseCase {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final QueueService queueService;
    private final ConcertService concertService;
    private final ConcertImageService concertImageService;
    private final SeatTypeRepository seatTypeRepository;

    @Override
    public List<Map<String, Object>> getAvailableDates(String token) {
        // 토큰 검증 후 예약 가능 날짜 반환
        queueService.validateQueueToken(token);

        // ConcertSchedule 테이블에서 모든 스케줄을 조회
        List<ConcertSchedule> schedules = concertScheduleRepository.findAll();

        // 각 스케줄에 대해 ConcertInfoResponse를 생성하고, 예약 가능 날짜와 함께 반환
        return schedules.stream()
                .map(schedule -> {
                    // 공연 정보 조회
                    Concert concert = concertService.getConcertById(schedule.getConcertId())
                            .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.CONCERT_NOT_FOUND.getMessage()));

                    // 공연 이미지 리스트 조회
                    List<String> imageUrls = concertImageService.getImagesByConcertId(concert.getConcertId());

                    double rating = Optional.ofNullable(concert.getRating())
                            .map(Double::parseDouble)
                            .orElse(0.0);

                    // ConcertInfoResponse 생성 (여러 이미지 포함)
                    ConcertInfoResponse concertInfo = ConcertInfoResponse.builder()
                            .concertTitle(concert.getTitle())
                            .posterUrl(concert.getPosterUrl())
                            .location(concert.getLocation())
                            .rating(rating)
                            .imageUrls(imageUrls)  // 여러 이미지 처리
                            .concertDate(schedule.getDate())
                            .duration(schedule.getDuration())
                            .ticketStartTime(schedule.getTicketStartTime())
                            .ticketEndTime(schedule.getTicketEndTime())
                            .build();

                    // 좌석 유형 정보 리스트 생성
                    List<SeatTypeInfoResponse> seatTypeInfoList = seatTypeRepository.findByConcertId(concert.getConcertId())
                            .stream()
                            .map(seatType -> SeatTypeInfoResponse.builder()
                                    .seatTypeName(seatType.getSeatTypeName())
                                    .price(Optional.ofNullable(seatType.getPrice())
                                            .map(BigDecimal::doubleValue)
                                            .orElse(0.0))
                                    .build())
                            .collect(Collectors.toList());

                    // 반환할 데이터 구조 생성
                    Map<String, Object> result = new HashMap<>();
                    result.put("concertInfo", concertInfo);
                    result.put("availableDate", schedule.getDate());
                    result.put("seatTypeInfoList", seatTypeInfoList);
                    return result;
                })
                .collect(Collectors.toList());
    }
}

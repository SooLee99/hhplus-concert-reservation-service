package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetAvailableDatesUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAvailableDatesService implements GetAvailableDatesUseCase {

    private final ConcertScheduleRepository concertScheduleRepository;

    @Override
    public List<LocalDateTime> getAvailableDates() {
        // ConcertSchedule 테이블에서 모든 스케줄을 조회하고 날짜만 리스트로 반환
        List<ConcertSchedule> schedules = concertScheduleRepository.findAll();
        return schedules.stream()
                .map(ConcertSchedule::getDate)
                .collect(Collectors.toList());
    }
}

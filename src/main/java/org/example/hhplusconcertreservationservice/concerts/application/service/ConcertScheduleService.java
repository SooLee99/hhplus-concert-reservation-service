package org.example.hhplusconcertreservationservice.concerts.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {

    private final ConcertScheduleRepository concertScheduleRepository;

    // 날짜에 맞는 스케줄 ID를 조회
    public Optional<Long> getScheduleIdByDate(LocalDateTime date) {
        return concertScheduleRepository.findScheduleIdByDateTime(date);
    }

    // 스케줄 ID로 상세 스케줄 정보 조회
    public Optional<ConcertSchedule> getConcertScheduleById(Long scheduleId) {
        return concertScheduleRepository.findById(scheduleId);
    }
}

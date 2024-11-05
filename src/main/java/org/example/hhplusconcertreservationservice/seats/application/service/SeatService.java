package org.example.hhplusconcertreservationservice.seats.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<Seat> findAllByScheduleId(Long scheduleId) {
        return seatRepository.findAllByScheduleId(scheduleId);
    }
}
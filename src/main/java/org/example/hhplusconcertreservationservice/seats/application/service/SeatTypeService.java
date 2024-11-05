package org.example.hhplusconcertreservationservice.seats.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeatTypeService {

    private final SeatTypeRepository seatTypeRepository;

    /**
     * seatTypeId를 기반으로 SeatType 정보를 조회하는 메서드.
     *
     * @param seatTypeId 좌석 타입 ID
     * @return Optional<SeatType>
     */
    public Optional<SeatType> getSeatTypeById(Long seatTypeId) {
        return seatTypeRepository.findById(seatTypeId);
    }
}

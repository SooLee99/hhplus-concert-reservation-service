package org.example.hhplusconcertreservationservice.concerts.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    /**
     * concertId를 기반으로 Concert 정보를 조회하는 메서드.
     *
     * @param concertId 공연 ID
     * @return Optional<Concert>
     */
    public Optional<Concert> getConcertById(Long concertId) {
        return concertRepository.findById(concertId);
    }
}

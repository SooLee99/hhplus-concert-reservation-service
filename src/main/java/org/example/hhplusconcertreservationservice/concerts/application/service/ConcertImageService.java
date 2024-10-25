package org.example.hhplusconcertreservationservice.concerts.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertImageService {

    private final ConcertImageRepository concertImageRepository;

    /**
     * concertId를 기반으로 Concert의 모든 이미지 URL을 조회하는 메서드.
     *
     * @param concertId 공연 ID
     * @return List<String> 이미지 URL 리스트
     */
    public List<String> getImagesByConcertId(Long concertId) {
        return concertImageRepository.findImageUrlsByConcertId(concertId);
    }
}

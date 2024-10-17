package org.example.hhplusconcertreservationservice.concerts.infrastructure;

import org.example.hhplusconcertreservationservice.concerts.domain.ConcertImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertImageRepository extends JpaRepository<ConcertImage, Long> {
    // 공연 ID로 이미지 조회
    List<ConcertImage> findByConcertId(Long concertId);
}
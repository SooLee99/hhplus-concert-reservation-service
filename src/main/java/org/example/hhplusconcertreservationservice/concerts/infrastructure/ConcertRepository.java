package org.example.hhplusconcertreservationservice.concerts.infrastructure;

import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {
    // 공연 제목으로 공연 조회
    Optional<Concert> findByTitle(String title);
}
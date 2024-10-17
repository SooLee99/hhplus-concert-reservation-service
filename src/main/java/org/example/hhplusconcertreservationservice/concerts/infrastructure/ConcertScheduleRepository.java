package org.example.hhplusconcertreservationservice.concerts.infrastructure;

import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    // 예약 가능한 날짜 목록 조회
    List<ConcertSchedule> findByDateAfter(LocalDateTime date);

    // 특정 공연 ID로 일정 조회
    List<ConcertSchedule> findByConcertId(Long concertId);
}
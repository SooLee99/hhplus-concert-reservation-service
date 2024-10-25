package org.example.hhplusconcertreservationservice.concerts.infrastructure;

import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    // 날짜로 스케줄 ID 조회
    @Query("SELECT cs.scheduleId FROM ConcertSchedule cs WHERE cs.date = :date")
    Optional<Long> findScheduleIdByDateTime(@Param("date") LocalDateTime date);

    // 특정 공연 ID로 일정 조회
    List<ConcertSchedule> findByConcertId(Long concertId);
}
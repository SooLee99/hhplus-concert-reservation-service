package org.example.hhplusconcertreservationservice.concerts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_schedules")
/// ConcertSchedule 엔티티: 콘서트 일정 정보를 저장하는 엔티티
public class ConcertSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(nullable = false)
    private LocalDateTime date;

    private int duration;

    @Column(name = "ticket_start_time")
    private LocalDateTime ticketStartTime;

    @Column(name = "ticket_end_time")
    private LocalDateTime ticketEndTime;

    @Builder
    public ConcertSchedule(Long scheduleId, Long concertId, LocalDateTime date, int duration, LocalDateTime ticketStartTime, LocalDateTime ticketEndTime) {
        this.scheduleId = scheduleId;
        this.concertId = concertId;
        this.date = date;
        this.duration = duration;
        this.ticketStartTime = ticketStartTime;
        this.ticketEndTime = ticketEndTime;
    }
}

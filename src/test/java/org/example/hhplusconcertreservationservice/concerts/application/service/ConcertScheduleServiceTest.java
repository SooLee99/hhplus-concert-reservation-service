package org.example.hhplusconcertreservationservice.concerts.application.service;

import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConcertScheduleServiceTest {

    @InjectMocks
    private ConcertScheduleService concertScheduleService;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("날짜에 맞는 스케줄 ID를 정상적으로 조회해야 한다")
    void getScheduleIdByDate_ShouldReturnScheduleId_WhenDateIsValid() {
        // Given
        LocalDateTime date = LocalDateTime.of(2024, 1, 1, 10, 0);
        Long expectedScheduleId = 1L;

        when(concertScheduleRepository.findScheduleIdByDateTime(date)).thenReturn(Optional.of(expectedScheduleId));

        // When
        Optional<Long> result = concertScheduleService.getScheduleIdByDate(date);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedScheduleId, result.get());
    }

    @Test
    @DisplayName("해당 날짜에 스케줄 ID가 없으면 빈 Optional을 반환해야 한다")
    void getScheduleIdByDate_ShouldReturnEmptyOptional_WhenNoScheduleFound() {
        // Given
        LocalDateTime date = LocalDateTime.of(2024, 1, 1, 10, 0);

        when(concertScheduleRepository.findScheduleIdByDateTime(date)).thenReturn(Optional.empty());

        // When
        Optional<Long> result = concertScheduleService.getScheduleIdByDate(date);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("스케줄 ID로 상세 스케줄 정보를 정상적으로 조회해야 한다")
    void getConcertScheduleById_ShouldReturnSchedule_WhenScheduleIdIsValid() {
        // Given
        Long scheduleId = 1L;
        ConcertSchedule expectedSchedule = ConcertSchedule.builder()
                .scheduleId(scheduleId)
                .concertId(1L)
                .build();

        when(concertScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(expectedSchedule));

        // When
        Optional<ConcertSchedule> result = concertScheduleService.getConcertScheduleById(scheduleId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedSchedule, result.get());
    }

    @Test
    @DisplayName("스케줄 ID로 조회된 스케줄 정보가 없을 경우 빈 Optional을 반환해야 한다")
    void getConcertScheduleById_ShouldReturnEmptyOptional_WhenNoScheduleFound() {
        // Given
        Long scheduleId = 1L;

        when(concertScheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // When
        Optional<ConcertSchedule> result = concertScheduleService.getConcertScheduleById(scheduleId);

        // Then
        assertFalse(result.isPresent());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

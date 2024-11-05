package org.example.hhplusconcertreservationservice.concerts.application.service;

import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("공연 ID로 공연 정보를 정상적으로 조회해야 한다")
    void getConcertById_ShouldReturnConcert_WhenConcertIdIsValid() {
        // Given
        Long concertId = 1L;
        Concert expectedConcert = Concert.builder()
                .concertId(concertId)
                .title("Test Concert")
                .build();

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(expectedConcert));

        // When
        Optional<Concert> result = concertService.getConcertById(concertId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedConcert, result.get());
    }

    @Test
    @DisplayName("공연 ID로 조회된 공연 정보가 없을 경우 빈 Optional을 반환해야 한다")
    void getConcertById_ShouldReturnEmptyOptional_WhenNoConcertFound() {
        // Given
        Long concertId = 1L;

        when(concertRepository.findById(concertId)).thenReturn(Optional.empty());

        // When
        Optional<Concert> result = concertService.getConcertById(concertId);

        // Then
        assertFalse(result.isPresent());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

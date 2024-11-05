package org.example.hhplusconcertreservationservice.concerts.application.service;

import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertImageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConcertImageServiceTest {

    @InjectMocks
    private ConcertImageService concertImageService;

    @Mock
    private ConcertImageRepository concertImageRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("공연 ID로 이미지 URL 리스트를 조회하면 정상적으로 반환해야 한다")
    void getImagesByConcertId_ShouldReturnImageUrls_WhenConcertIdIsValid() {
        // Given
        Long concertId = 1L;
        List<String> expectedUrls = Arrays.asList("image1.jpg", "image2.jpg");

        when(concertImageRepository.findImageUrlsByConcertId(concertId)).thenReturn(expectedUrls);

        // When
        List<String> result = concertImageService.getImagesByConcertId(concertId);

        // Then
        assertNotNull(result);
        assertEquals(expectedUrls.size(), result.size());
        assertEquals(expectedUrls, result);
    }

    @Test
    @DisplayName("공연 ID로 이미지 URL이 없을 경우 빈 리스트를 반환해야 한다")
    void getImagesByConcertId_ShouldReturnEmptyList_WhenNoImagesFound() {
        // Given
        Long concertId = 1L;

        when(concertImageRepository.findImageUrlsByConcertId(concertId)).thenReturn(Collections.emptyList());

        // When
        List<String> result = concertImageService.getImagesByConcertId(concertId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

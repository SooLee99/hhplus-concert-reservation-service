package org.example.hhplusconcertreservationservice.reservations.application.service;

import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertImageService;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertService;
import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.ConcertInfoResponse;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.SeatTypeInfoResponse;
import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatTypeRepository;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetAvailableDatesServiceTest {

    @InjectMocks
    private GetAvailableDatesService getAvailableDatesService;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @Mock
    private QueueService queueService;

    @Mock
    private ConcertService concertService;

    @Mock
    private ConcertImageService concertImageService;

    @Mock
    private SeatTypeRepository seatTypeRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 토큰으로 예약 가능한 날짜를 반환해야 한다")
    void getAvailableDates_ShouldReturnDates_WhenTokenIsValid() {
        // Given
        String token = "valid_token";

        doNothing().when(queueService).validateQueueToken(token);

        ConcertSchedule schedule = ConcertSchedule.builder()
                .concertId(1L)
                .date(LocalDateTime.now())
                .duration(120)
                .ticketStartTime(LocalDateTime.now())
                .ticketEndTime(LocalDateTime.now().plusHours(1))
                .build();

        when(concertScheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        Concert concert = Concert.builder()
                .title("Test Concert")
                .posterUrl("poster_url")
                .location("Test Location")
                .rating("4.5")
                .build();

        when(concertService.getConcertById(1L)).thenReturn(Optional.of(concert));
        when(concertImageService.getImagesByConcertId(1L)).thenReturn(Arrays.asList("image1", "image2"));

        SeatType seatType = SeatType.builder()
                .seatTypeName("VIP")
                .price(BigDecimal.valueOf(100.0))
                .concertId(1L)
                .build();

        when(seatTypeRepository.findByConcertId(1L)).thenReturn(Arrays.asList(seatType));

        // When
        List<Map<String, Object>> result = getAvailableDatesService.getAvailableDates(token);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, Object> data = result.get(0);
        assertTrue(data.containsKey("concertInfo"));
        assertTrue(data.containsKey("availableDate"));
        assertTrue(data.containsKey("seatTypeInfoList"));
    }

    @Test
    @DisplayName("유효하지 않은 토큰인 경우 예외가 발생해야 한다")
    void getAvailableDates_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String token = "invalid_token";

        doThrow(new IllegalArgumentException("유효하지 않은 토큰입니다."))
                .when(queueService).validateQueueToken(token);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                getAvailableDatesService.getAvailableDates(token)
        );
        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("공연 스케줄이 없는 경우 빈 리스트를 반환해야 한다")
    void getAvailableDates_ShouldReturnEmptyList_WhenNoSchedulesExist() {
        // Given
        String token = "valid_token";

        doNothing().when(queueService).validateQueueToken(token);

        when(concertScheduleRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Map<String, Object>> result = getAvailableDatesService.getAvailableDates(token);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

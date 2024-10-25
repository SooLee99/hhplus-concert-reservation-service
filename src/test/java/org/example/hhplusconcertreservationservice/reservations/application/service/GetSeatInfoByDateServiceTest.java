package org.example.hhplusconcertreservationservice.reservations.application.service;

import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertImageService;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertScheduleService;
import org.example.hhplusconcertreservationservice.concerts.application.service.ConcertService;
import org.example.hhplusconcertreservationservice.concerts.domain.Concert;
import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;
import org.example.hhplusconcertreservationservice.seats.application.service.SeatService;
import org.example.hhplusconcertreservationservice.seats.application.service.SeatTypeService;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetSeatInfoByDateServiceTest {

    @InjectMocks
    private GetSeatInfoByDateService getSeatInfoByDateService;

    @Mock
    private SeatService seatService;

    @Mock
    private SeatTypeService seatTypeService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ConcertService concertService;

    @Mock
    private ConcertScheduleService concertScheduleService;

    @Mock
    private ConcertImageService concertImageService;

    @Mock
    private QueueService queueService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 날짜와 토큰으로 좌석 정보를 반환해야 한다")
    void getSeatInfoByDateTime_ShouldReturnSeatInfo_WhenInputIsValid() {
        // Given
        String token = "valid_token";
        LocalDateTime date = LocalDateTime.now();

        doNothing().when(queueService).validateQueueToken(token);

        Long scheduleId = 1L;
        when(concertScheduleService.getScheduleIdByDate(date)).thenReturn(Optional.of(scheduleId));

        Seat seat1 = Seat.builder()
                .seatId(1L)
                .seatNumber(1)
                .seatTypeId(10L)
                .build();

        Seat seat2 = Seat.builder()
                .seatId(2L)
                .seatNumber(2)
                .seatTypeId(10L)
                .build();

        when(seatService.findAllByScheduleId(scheduleId)).thenReturn(Arrays.asList(seat1, seat2));

        when(reservationRepository.findReservedSeatIdsByScheduleId(scheduleId)).thenReturn(Arrays.asList(2L));

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .date(date)
                .duration(120)
                .ticketStartTime(date.minusHours(1))
                .ticketEndTime(date.plusHours(1))
                .build();

        when(concertScheduleService.getConcertScheduleById(scheduleId)).thenReturn(Optional.of(concertSchedule));

        SeatType seatType = SeatType.builder()
                .seatTypeName("VIP")
                .price(BigDecimal.valueOf(100.0))
                .concertId(100L)
                .build();

        when(seatTypeService.getSeatTypeById(10L)).thenReturn(Optional.of(seatType));

        Concert concert = Concert.builder()
                .title("Test Concert")
                .posterUrl("poster_url")
                .location("Test Location")
                .rating("4.5")
                .build();

        when(concertService.getConcertById(100L)).thenReturn(Optional.of(concert));
        when(concertImageService.getImagesByConcertId(100L)).thenReturn(Arrays.asList("image1", "image2"));

        // When
        Map<String, Object> response = getSeatInfoByDateService.getSeatInfoByDateTime(date, token);

        // Then
        assertNotNull(response);
        assertTrue(response.containsKey("concertInfo"));
        assertTrue(response.containsKey("seatInfoList"));

        List<SeatInfoResponse> seatInfoList = (List<SeatInfoResponse>) response.get("seatInfoList");
        assertEquals(2, seatInfoList.size());

        SeatInfoResponse seatInfo1 = seatInfoList.get(0);
        SeatInfoResponse seatInfo2 = seatInfoList.get(1);

        assertEquals(1L, seatInfo1.getSeatId());
        assertEquals(SeatStatus.AVAILABLE, seatInfo1.getStatus());

        assertEquals(2L, seatInfo2.getSeatId());
        assertEquals(SeatStatus.RESERVED, seatInfo2.getStatus());
    }

    @Test
    @DisplayName("유효하지 않은 토큰인 경우 예외가 발생해야 한다")
    void getSeatInfoByDateTime_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String token = "invalid_token";
        LocalDateTime date = LocalDateTime.now();

        doThrow(new IllegalArgumentException("유효하지 않은 토큰입니다."))
                .when(queueService).validateQueueToken(token);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                getSeatInfoByDateService.getSeatInfoByDateTime(date, token)
        );
        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("해당 날짜에 스케줄이 없는 경우 예외가 발생해야 한다")
    void getSeatInfoByDateTime_ShouldThrowException_WhenScheduleDoesNotExist() {
        // Given
        String token = "valid_token";
        LocalDateTime date = LocalDateTime.now();

        doNothing().when(queueService).validateQueueToken(token);

        when(concertScheduleService.getScheduleIdByDate(date)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                getSeatInfoByDateService.getSeatInfoByDateTime(date, token)
        );
        assertEquals("해당 날짜에 대한 공연 스케줄을 찾을 수 없습니다.", exception.getMessage());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

package org.example.hhplusconcertreservationservice.reservations.application.service;

import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.CreateReservationResponse;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateReservationServiceTest {

    @InjectMocks
    private CreateReservationService createReservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRequestValidator reservationRequestValidator;

    @Mock
    private QueueService queueService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 요청으로 예약이 정상적으로 생성되어야 한다")
    void createReservation_ShouldCreateReservation_WhenRequestIsValid() {
        // Given
        String token = "valid_token";
        Long userId = 1L;
        Long seatId = 100L;

        CreateReservationRequest request = new CreateReservationRequest(token, seatId);

        Seat seat = Seat.builder()
                .seatId(seatId)
                .scheduleId(10L)
                .seatNumber(1)
                .seatTypeId(5L)
                .isReserved(false)
                .build();

        when(queueService.getUserIdFromToken(token)).thenReturn(userId);
        doNothing().when(reservationRequestValidator).validate(request);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        Reservation savedReservation = Reservation.builder()
                .userId(userId)
                .seatId(seatId)
                .scheduleId(seat.getScheduleId())
                .reservationStatus(ReservationStatus.PENDING)
                .reservationTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // When
        CreateReservationResponse response = createReservationService.createReservation(request);

        // Then
        assertNotNull(response);
        assertEquals(savedReservation.getReservationId(), response.getReservationId());
    }

    @Test
    @DisplayName("좌석이 이미 예약된 경우 SeatNotAvailableException이 발생해야 한다")
    void createReservation_ShouldThrowException_WhenSeatAlreadyReserved() {
        // Given
        String token = "valid_token";
        Long userId = 1L;
        Long seatId = 100L;

        CreateReservationRequest request = new CreateReservationRequest(token, seatId);

        Seat seat = Seat.builder()
                .seatId(seatId)
                .isReserved(true) // 이미 예약됨
                .build();

        when(queueService.getUserIdFromToken(token)).thenReturn(userId);
        doNothing().when(reservationRequestValidator).validate(request);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // When & Then - 좌석이 이미 예약된 경우
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () ->
                createReservationService.createReservation(request)
        );
        assertEquals(ExceptionMessage.SEAT_ALREADY_RESERVED.getMessage(), exception1.getMessage());
    }

    @Test
    @DisplayName("좌석이 존재하지 않는 경우 SeatNotAvailableException이 발생해야 한다")
    void createReservation_ShouldThrowException_WhenSeatDoesNotExist() {
        // Given
        String token = "valid_token";
        Long userId = 1L;
        Long seatId = 100L;

        CreateReservationRequest request = new CreateReservationRequest(token, seatId);

        when(queueService.getUserIdFromToken(token)).thenReturn(userId);
        doNothing().when(reservationRequestValidator).validate(request);
        when(seatRepository.findById(seatId)).thenReturn(Optional.empty());

        // When & Then - 좌석을 찾을 수 없는 경우
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () ->
                createReservationService.createReservation(request)
        );
        assertEquals(ExceptionMessage.INVALID_SEAT.getMessage(), exception2.getMessage());
    }

    @Test
    @DisplayName("요청이 유효하지 않은 경우 IllegalArgumentException이 발생해야 한다")
    void createReservation_ShouldThrowException_WhenRequestIsInvalid() {
        // Given
        String token = null; // 토큰이 없음
        Long seatId = 100L;

        CreateReservationRequest request = new CreateReservationRequest(token, seatId);

        doThrow(new IllegalArgumentException("필수 입력 값이 누락되었습니다."))
                .when(reservationRequestValidator).validate(request);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                createReservationService.createReservation(request)
        );
        assertEquals("필수 입력 값이 누락되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("유효하지 않은 토큰인 경우 예외가 발생해야 한다")
    void createReservation_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String token = "invalid_token";
        Long seatId = 100L;

        CreateReservationRequest request = new CreateReservationRequest(token, seatId);

        doThrow(new IllegalArgumentException("유효하지 않은 토큰입니다."))
                .when(queueService).getUserIdFromToken(token);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                createReservationService.createReservation(request)
        );
        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("예약 생성 중 오류 발생 시 ReservationCreationException이 발생하고 좌석 예약이 취소되어야 한다")
    void createReservation_ShouldRollbackSeatReservation_WhenExceptionOccurs() {
        // Given
        String token = "valid_token";
        Long userId = 1L;
        Long seatId = 100L;

        CreateReservationRequest request = new CreateReservationRequest(token, seatId);

        Seat seat = Seat.builder()
                .seatId(seatId)
                .scheduleId(10L)
                .seatNumber(1)
                .seatTypeId(5L)
                .isReserved(false)
                .build();

        when(queueService.getUserIdFromToken(token)).thenReturn(userId);
        doNothing().when(reservationRequestValidator).validate(request);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // 예약 저장 시 예외 발생
        when(reservationRepository.save(any(Reservation.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                createReservationService.createReservation(request)
        );
        assertEquals(ExceptionMessage.QUEUE_CREATION_FAILED.getMessage(), exception.getMessage());
        assertFalse(seat.isReserved()); // 좌석 예약이 취소되었는지 확인
   }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

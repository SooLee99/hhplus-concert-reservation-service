package org.example.hhplusconcertreservationservice.reservations.application.service;

import org.example.hhplusconcertreservationservice.concerts.domain.ConcertSchedule;
import org.example.hhplusconcertreservationservice.concerts.infrastructure.ConcertScheduleRepository;
import org.example.hhplusconcertreservationservice.reservations.application.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.application.dto.response.CreateReservationResponse;
import org.example.hhplusconcertreservationservice.reservations.application.exception.SeatNotAvailableException;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.SeatStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("예약 가능한 날짜 목록을 성공적으로 조회한다")
    void testGetAvailableDates_Success() {
        // Given
        ConcertSchedule schedule1 = ConcertSchedule.builder()
                .concertId(1L)
                .date(LocalDateTime.of(2024, 10, 20, 19, 0))
                .duration(120)
                .ticketStartTime(LocalDateTime.of(2024, 10, 1, 10, 0))
                .ticketEndTime(LocalDateTime.of(2024, 10, 10, 18, 0))
                .build();

        ConcertSchedule schedule2 = ConcertSchedule.builder()
                .concertId(2L)
                .date(LocalDateTime.of(2024, 10, 21, 19, 0))
                .duration(120)
                .ticketStartTime(LocalDateTime.of(2024, 10, 1, 10, 0))
                .ticketEndTime(LocalDateTime.of(2024, 10, 11, 18, 0))
                .build();

        when(concertScheduleRepository.findAll()).thenReturn(Arrays.asList(schedule1, schedule2));

        // When
        List<LocalDateTime> availableDates = reservationService.getAvailableDates();

        // Then
        assertNotNull(availableDates);
        assertEquals(2, availableDates.size());
        assertTrue(availableDates.contains(schedule1.getDate()));
        assertTrue(availableDates.contains(schedule2.getDate()));

        verify(concertScheduleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("예약 가능한 날짜가 없을 때 빈 목록을 반환한다")
    void testGetAvailableDates_EmptyList() {
        // Given
        when(concertScheduleRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<LocalDateTime> availableDates = reservationService.getAvailableDates();

        // Then
        assertNotNull(availableDates);
        assertTrue(availableDates.isEmpty());

        verify(concertScheduleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("특정 날짜의 좌석 정보를 성공적으로 조회한다")
    void testGetSeatInfoByDate_Success() {
        // Given
        LocalDateTime date = LocalDateTime.of(2024, 10, 20, 19, 0);
        Seat seat1 = Seat.builder()
                .seatId(1L) // seatId 수동 설정
                .seatTypeId(1L)
                .scheduleId(1L)
                .seatNumber(1)
                .isReserved(false)
                .build();

        Seat seat2 = Seat.builder()
                .seatId(2L) // seatId 수동 설정
                .seatTypeId(2L)
                .scheduleId(1L)
                .seatNumber(2)
                .isReserved(false)
                .build();

        Seat seat3 = Seat.builder()
                .seatId(3L) // seatId 수동 설정
                .seatTypeId(3L)
                .scheduleId(1L)
                .seatNumber(3)
                .isReserved(false)
                .build();

        when(seatRepository.findAll()).thenReturn(Arrays.asList(seat1, seat2, seat3));
        when(reservationRepository.findReservedSeatIdsByDate(date)).thenReturn(Arrays.asList(1L, 3L));

        // When
        List<SeatInfoResponse> seatInfoResponses = reservationService.getSeatInfoByDate(date);

        // Then
        assertNotNull(seatInfoResponses);
        assertEquals(3, seatInfoResponses.size());

        for (SeatInfoResponse response : seatInfoResponses) {
            assertNotNull(response.getSeatId()); // SeatId가 null이 아닌지 확인
            if (response.getSeatId().equals(1L) || response.getSeatId().equals(3L)) {
                assertEquals(SeatStatus.RESERVED, response.getSeatStatus());
            } else if (response.getSeatId().equals(2L)) {
                assertEquals(SeatStatus.AVAILABLE, response.getSeatStatus());
            } else {
                fail("알 수 없는 좌석 ID: " + response.getSeatId());
            }
        }

        verify(seatRepository, times(1)).findAll();
        verify(reservationRepository, times(1)).findReservedSeatIdsByDate(date);
    }

    @Test
    @DisplayName("특정 날짜에 예약된 좌석이 없을 때 모든 좌석을 AVAILABLE 상태로 반환한다")
    void testGetSeatInfoByDate_NoReservations() {
        // Given
        LocalDateTime date = LocalDateTime.of(2024, 10, 20, 19, 0);
        Seat seat1 = Seat.builder()
                .seatTypeId(1L)
                .scheduleId(1L)
                .seatNumber(1)
                .isReserved(false)
                .build();

        Seat seat2 = Seat.builder()
                .seatTypeId(2L)
                .scheduleId(1L)
                .seatNumber(2)
                .isReserved(false)
                .build();

        when(seatRepository.findAll()).thenReturn(Arrays.asList(seat1, seat2));
        when(reservationRepository.findReservedSeatIdsByDate(date)).thenReturn(Collections.emptyList());

        // When
        List<SeatInfoResponse> seatInfoResponses = reservationService.getSeatInfoByDate(date);

        // Then
        assertNotNull(seatInfoResponses);
        assertEquals(2, seatInfoResponses.size());

        for (SeatInfoResponse response : seatInfoResponses) {
            assertEquals(SeatStatus.AVAILABLE, response.getSeatStatus());
        }

        verify(seatRepository, times(1)).findAll();
        verify(reservationRepository, times(1)).findReservedSeatIdsByDate(date);
    }

    @Test
    @DisplayName("좌석 예약을 성공적으로 생성한다")
    void testCreateReservation_Success() {
        // Given
        Long userId = 1L;
        Long seatId = 1L;
        Long scheduleId = 1L;
        Long paymentId = 1L;

        CreateReservationRequest request = new CreateReservationRequest(userId, scheduleId, seatId, paymentId);
        Seat seat = Seat.builder()
                .seatTypeId(1L)  // 예시로 seatTypeId를 추가
                .scheduleId(scheduleId)
                .seatNumber(1)
                .isReserved(false)
                .build();

        when(seatRepository.findBySeatIdAndIsReservedFalse(seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .seatId(seatId)
                .scheduleId(scheduleId)
                .paymentId(paymentId)
                .reservationStatus(ReservationStatus.PENDING)
                .expirationTime(LocalDateTime.now().plusMinutes(5))  // 5분 후 만료 시간 설정
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // When
        CreateReservationResponse response = reservationService.createReservation(request);

        // Then
        assertNotNull(response);
        assertEquals(reservation.getReservationId(), response.getReservationId());
        assertEquals(reservation.getUserId(), response.getUserId());
        assertEquals(reservation.getSeatId(), response.getSeatId());
        assertEquals(reservation.getScheduleId(), response.getScheduleId());

        // Enum을 name()으로 변환해 String으로 비교
        assertEquals(reservation.getReservationStatus().name(), response.getReservationStatus());

        verify(seatRepository, times(1)).findBySeatIdAndIsReservedFalse(seatId);
        verify(seatRepository, times(1)).save(any(Seat.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("이미 예약된 좌석을 예약하려 할 때 예외를 발생시킨다")
    void testCreateReservation_SeatNotAvailable() {
        // Given
        Long userId = 1L;
        Long seatId = 1L;
        Long scheduleId = 1L;
        Long paymentId = 1L;

        CreateReservationRequest request = new CreateReservationRequest(userId, seatId, scheduleId, paymentId);

        when(seatRepository.findBySeatIdAndIsReservedFalse(seatId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SeatNotAvailableException.class, () -> reservationService.createReservation(request));

        verify(seatRepository, times(1)).findBySeatIdAndIsReservedFalse(seatId);
        verify(seatRepository, never()).save(any(Seat.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("만료된 예약을 성공적으로 처리한다")
    void testExpirePendingReservations_Success() {
        // Given
        Reservation reservation1 = Reservation.builder()
                .userId(1L)
                .seatId(1L)
                .scheduleId(1L)
                .reservationStatus(ReservationStatus.PENDING)  // enum 타입 사용
                .expirationTime(LocalDateTime.now().minusMinutes(1))
                .build();

        Reservation reservation2 = Reservation.builder()
                .userId(2L)
                .seatId(2L)
                .scheduleId(1L)
                .reservationStatus(ReservationStatus.PENDING)  // enum 타입 사용
                .expirationTime(LocalDateTime.now().minusMinutes(2))
                .build();

        when(reservationRepository.findByReservationStatusAndExpirationTimeBefore(
                eq(ReservationStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(reservation1, reservation2));

        Seat seat1 = Seat.builder()
                .seatTypeId(1L)
                .scheduleId(1L)
                .seatNumber(1)
                .isReserved(true)
                .build();

        Seat seat2 = Seat.builder()
                .seatTypeId(2L)
                .scheduleId(1L)
                .seatNumber(2)
                .isReserved(true)
                .build();

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));
        when(seatRepository.findById(2L)).thenReturn(Optional.of(seat2));

        // When
        reservationService.expirePendingReservations();

        // Then
        assertEquals(ReservationStatus.EXPIRED, reservation1.getReservationStatus()); // enum 타입 비교
        assertEquals(ReservationStatus.EXPIRED, reservation2.getReservationStatus()); // enum 타입 비교
        assertFalse(seat1.isReserved());
        assertFalse(seat2.isReserved());

        verify(reservationRepository, times(1)).findByReservationStatusAndExpirationTimeBefore(
                eq(ReservationStatus.PENDING), any(LocalDateTime.class));
        verify(reservationRepository, times(2)).save(any(Reservation.class));
        verify(seatRepository, times(2)).findById(anyLong());
        verify(seatRepository, times(2)).save(any(Seat.class));
    }

    @Test
    @DisplayName("만료 처리 시 좌석을 찾을 수 없으면 예외를 발생시킨다")
    void testExpirePendingReservations_SeatNotFound() {
        // Given
        Reservation reservation = Reservation.builder()
                .userId(1L)
                .seatId(1L)
                .scheduleId(1L)
                .reservationStatus(ReservationStatus.PENDING)
                .expirationTime(LocalDateTime.now().minusMinutes(1))
                .build();

        when(reservationRepository.findByReservationStatusAndExpirationTimeBefore(
                eq(ReservationStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(reservation));

        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> reservationService.expirePendingReservations());
        assertEquals("해당 좌석을 찾을 수 없습니다.", exception.getMessage());

        verify(reservationRepository, times(1)).findByReservationStatusAndExpirationTimeBefore(
                eq(ReservationStatus.PENDING), any(LocalDateTime.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(seatRepository, times(1)).findById(1L);
        verify(seatRepository, never()).save(any(Seat.class));
    }
}
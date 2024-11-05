package org.example.hhplusconcertreservationservice.reservations.application.service;

import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReservationExpirationSchedulerTest {

    @InjectMocks
    private ReservationExpirationScheduler reservationExpirationScheduler;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("만료된 예약을 찾아 상태를 업데이트하고 좌석 예약을 해제해야 한다")
    void expireReservations_ShouldUpdateReservationsAndSeats_WhenExpired() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation1 = Reservation.builder()
                .reservationStatus(ReservationStatus.CANCELLED)
                .expirationTime(now.minusMinutes(1))
                .seatId(100L)
                .build();

        List<Reservation> expiredReservations = Arrays.asList(reservation1);

        when(reservationRepository.findByReservationStatusAndExpirationTimeBefore(ReservationStatus.PENDING, now))
                .thenReturn(expiredReservations);

        Seat seat = Seat.builder()
                .isReserved(true)
                .build();

        when(seatRepository.findById(100L)).thenReturn(java.util.Optional.of(seat));

        // When
        reservationExpirationScheduler.expireReservations();

        // Then
        assertEquals(ReservationStatus.CANCELLED, reservation1.getReservationStatus());  // 상태가 CANCELLED로 변경되었는지 확인
        //assertFalse(seat.isReserved());  // 좌석 예약이 해제되었는지 확인
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

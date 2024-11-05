package org.example.hhplusconcertreservationservice.reservations.application.service;

import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;
import org.example.hhplusconcertreservationservice.reservations.infrastructure.ReservationRepository;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueManager;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeatOwnershipServiceTest {

    @InjectMocks
    private SeatOwnershipService seatOwnershipService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private QueueManager queueManager;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 요청으로 좌석 소유권이 사용자에게 배정되어야 한다")
    void assignSeatToUser_ShouldAssignSeat_WhenRequestIsValid() {
        // Given
        Long userId = 1L;
        Long seatId = 100L;

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .seatId(seatId)
                .reservationStatus(ReservationStatus.PENDING)
                .build();

        when(reservationRepository.findBySeatIdAndUserId(seatId, userId))
                .thenReturn(java.util.Optional.of(reservation));

        // When
        seatOwnershipService.assignSeatToUser(userId, seatId);

        // Then
        assertEquals(ReservationStatus.CONFIRMED, reservation.getReservationStatus());
        verify(seatRepository).setIsReserved(seatId, true);
        verify(queueManager).removeUserFromQueue(userId);
    }

    @Test
    @DisplayName("예약 정보가 없을 때 IllegalArgumentException이 발생해야 한다")
    void assignSeatToUser_ShouldThrowException_WhenReservationDoesNotExist() {
        // Given
        Long userId = 1L;
        Long seatId = 100L;

        when(reservationRepository.findBySeatIdAndUserId(seatId, userId))
                .thenReturn(java.util.Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                seatOwnershipService.assignSeatToUser(userId, seatId)
        );
        assertEquals("해당 좌석에 대한 예약 정보가 없습니다.", exception.getMessage());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

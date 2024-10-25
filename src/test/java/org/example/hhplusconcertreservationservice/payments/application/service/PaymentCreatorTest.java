package org.example.hhplusconcertreservationservice.payments.application.service;

import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.example.hhplusconcertreservationservice.payments.domain.Payment;
import org.example.hhplusconcertreservationservice.payments.domain.PaymentStatus;
import org.example.hhplusconcertreservationservice.payments.infrastructure.PaymentRepository;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentCreatorTest {

    @InjectMocks
    private PaymentCreator paymentCreator;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatTypeRepository seatTypeRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("좌석과 좌석 유형이 유효하면 결제 금액을 올바르게 반환해야 한다")
    void determineAmount_ShouldReturnCorrectAmount_WhenSeatAndSeatTypeAreValid() {
        // Given
        PaymentRequest request = new PaymentRequest(1L, "valid_token");
        Seat seat = Seat.builder()
                .seatId(1L)
                .seatTypeId(2L)
                .build();
        SeatType seatType = SeatType.builder()
                .seatTypeId(2L)
                .price(BigDecimal.valueOf(100.0))
                .build();

        when(seatRepository.findBySeatId(request.getSeatId())).thenReturn(Optional.of(seat));
        when(seatTypeRepository.findBySeatTypeId(seat.getSeatTypeId())).thenReturn(Optional.of(seatType));

        // When
        BigDecimal amount = paymentCreator.determineAmount(request);

        // Then
        assertEquals(BigDecimal.valueOf(100.0), amount);
    }

    @Test
    @DisplayName("좌석을 찾을 수 없으면 예외를 발생시켜야 한다")
    void determineAmount_ShouldThrowException_WhenSeatNotFound() {
        // Given
        PaymentRequest request = new PaymentRequest(1L, "valid_token");

        when(seatRepository.findBySeatId(request.getSeatId())).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentCreator.determineAmount(request));
        assertEquals("해당 좌석을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("좌석 유형을 찾을 수 없으면 예외를 발생시켜야 한다")
    void determineAmount_ShouldThrowException_WhenSeatTypeNotFound() {
        // Given
        PaymentRequest request = new PaymentRequest(1L, "valid_token");
        Seat seat = Seat.builder()
                .seatId(1L)
                .seatTypeId(2L)
                .build();

        when(seatRepository.findBySeatId(request.getSeatId())).thenReturn(Optional.of(seat));
        when(seatTypeRepository.findBySeatTypeId(seat.getSeatTypeId())).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentCreator.determineAmount(request));
        assertEquals("해당 좌석 유형을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 상태가 PENDING인 결제를 생성하고 저장해야 한다")
    void createPendingPayment_ShouldCreateAndSavePendingPayment() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(100.0);
        Long userId = 1L;
        PaymentRequest request = new PaymentRequest(1L, "valid_token");

        Payment payment = Payment.builder()
                .userId(userId)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        Payment result = paymentCreator.createPendingPayment(request, amount, userId);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    @DisplayName("결제 성공 처리 중 예외 발생 시 IllegalArgumentException을 발생시켜야 한다")
    void handlePaymentSuccess_ShouldThrowException_WhenErrorOccurs() {
        // Given
        Payment payment = Payment.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        doThrow(new RuntimeException("Database error"))
                .when(paymentRepository).save(payment);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentCreator.handlePaymentSuccess(payment));
        assertEquals("결제 처리 중 오류가 발생했습니다.", exception.getMessage());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

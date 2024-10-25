package org.example.hhplusconcertreservationservice.payments.application.service;

import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.example.hhplusconcertreservationservice.reservations.application.service.SeatOwnershipService;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.Reservation;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.application.service.balance.UseBalanceService;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.AfterEach;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessPaymentServiceTest {

    @InjectMocks
    private ProcessPaymentService processPaymentService;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private PaymentCreator paymentCreator;

    @Mock
    private SeatOwnershipAssigner seatOwnershipAssigner;

    @Mock
    private QueueTokenManager queueTokenManager;

    @Mock
    private UseBalanceService useBalanceService;

    @Mock
    private PaymentResponseFactory paymentResponseFactory;

    @Mock
    private SeatOwnershipService seatOwnershipService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("결제 요청이 null일 경우 IllegalArgumentException을 발생시켜야 한다")
    void processPayment_ShouldThrowException_WhenRequestIsNull() {
        // Given
        PaymentRequest request = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                processPaymentService.processPayment(request));
        assertEquals("결제 요청에 필수 정보가 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("토큰이 null일 경우 IllegalArgumentException을 발생시켜야 한다")
    void processPayment_ShouldThrowException_WhenTokenIsNull() {
        // Given
        PaymentRequest request = new PaymentRequest(1L, null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                processPaymentService.processPayment(request));
        assertEquals("결제 요청에 필수 정보가 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("잔액이 부족할 경우 IllegalArgumentException을 발생시켜야 한다")
    void processPayment_ShouldThrowException_WhenInsufficientBalance() {
        // Given
        PaymentRequest request = new PaymentRequest(1L, "valid_token");
        Long userId = 1L;
        Long seatId = request.getSeatId(); // 요청의 seatId 가져오기
        BigDecimal amount = BigDecimal.valueOf(100.0);

        // 사용자 잔액과 관련된 객체 설정
        UserBalance userBalance = UserBalance.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(50.0)) // 잔액 부족 (결제 금액보다 적음)
                .build();

        UserBalanceResponse userBalanceResponse = new UserBalanceResponse(userBalance);

        // 예약 확인 과정 추가 - findReservationForSeat이 Reservation을 반환한다고 가정
        Reservation mockReservation = mock(Reservation.class);  // 반환할 예약 객체 모킹
        when(seatOwnershipService.findReservationForSeat(userId, seatId)).thenReturn(mockReservation);

        // 토큰 검증 및 잔액 관련 모킹
        when(queueTokenManager.validateToken(request.getToken())).thenReturn(userId);
        when(paymentCreator.determineAmount(request)).thenReturn(amount);
        when(useBalanceService.getUserBalance(any(ChargeBalanceRequest.class))).thenReturn(userBalanceResponse);

        // When & Then: 잔액 부족으로 예외 발생 여부 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                processPaymentService.processPayment(request));

        // 예외 메시지 검증
        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 금액이 null일 경우 IllegalArgumentException을 발생시켜야 한다")
    void processPayment_ShouldThrowException_WhenAmountIsNull() {
        // Given
        PaymentRequest request = new PaymentRequest(1L, "valid_token");

        when(queueTokenManager.validateToken(request.getToken())).thenReturn(1L);
        when(paymentCreator.determineAmount(request)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                processPaymentService.processPayment(request));
        assertEquals("결제 금액을 확인할 수 없습니다.", exception.getMessage());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}

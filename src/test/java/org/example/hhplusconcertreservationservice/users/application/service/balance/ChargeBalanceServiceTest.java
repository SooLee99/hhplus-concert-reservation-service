package org.example.hhplusconcertreservationservice.users.application.service.balance;

import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceHistoryRepository;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChargeBalanceServiceTest {

    @InjectMocks
    private ChargeBalanceService chargeBalanceService;

    @Mock
    private UserBalanceHistoryRepository balanceHistoryRepository;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private BalanceValidationService balanceValidationService;

    @Mock
    private UserBalanceQueryService userBalanceQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 요청으로 잔액이 충전되어야 한다")
    void chargeBalance_ShouldChargeBalance_WhenRequestIsValid() {
        // Given
        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1000);
        ChargeBalanceRequest request = new ChargeBalanceRequest(userId, amount);

        UserBalance userBalance = UserBalance.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();

        when(userBalanceQueryService.getUserBalanceEntity(userId)).thenReturn(userBalance);

        // When
        UserBalanceResponse response = chargeBalanceService.chargeBalance(request);

        // Then
        assertEquals(userId, response.getUserId());
        assertEquals(amount, response.getBalance());
    }

    @Test
    @DisplayName("유효하지 않은 금액으로 충전 시 IllegalArgumentException이 발생한다")
    void chargeBalance_ShouldThrowException_WhenAmountIsInvalid() {
        // Given
        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(-1000);
        ChargeBalanceRequest request = new ChargeBalanceRequest(userId, amount);

        doThrow(new IllegalArgumentException("충전 금액은 0보다 커야 합니다."))
                .when(balanceValidationService).validateChargeAmount(amount);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                chargeBalanceService.chargeBalance(request)
        );
        assertEquals("충전 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 잔액이 없을 때 새로운 잔액이 생성되어야 한다")
    void chargeBalance_ShouldCreateNewBalance_WhenUserBalanceDoesNotExist() {
        // Given
        Long userId = 2L;
        BigDecimal amount = BigDecimal.valueOf(5000);
        ChargeBalanceRequest request = new ChargeBalanceRequest(userId, amount);

        when(userBalanceQueryService.getUserBalanceEntity(userId))
                .thenReturn(UserBalance.builder().userId(userId).balance(BigDecimal.ZERO).build());

        // When
        UserBalanceResponse response = chargeBalanceService.chargeBalance(request);

        // Then
        assertEquals(userId, response.getUserId());
        assertEquals(amount, response.getBalance());
    }
}

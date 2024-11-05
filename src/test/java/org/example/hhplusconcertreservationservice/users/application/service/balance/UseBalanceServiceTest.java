package org.example.hhplusconcertreservationservice.users.application.service.balance;

import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.domain.TransactionType;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.domain.UserBalanceHistory;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceHistoryRepository;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UseBalanceServiceTest {

    @InjectMocks
    private UseBalanceService useBalanceService;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private UserBalanceHistoryRepository balanceHistoryRepository;

    @Mock
    private BalanceValidationService balanceValidationService;

    @Mock
    private UserBalanceQueryService userBalanceQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 요청으로 잔액이 차감되어야 한다")
    void useBalance_ShouldDeductBalance_WhenRequestIsValid() {
        // Given
        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(500);
        ChargeBalanceRequest request = new ChargeBalanceRequest(userId, amount);

        UserBalance userBalance = UserBalance.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(userBalanceQueryService.getUserBalanceEntity(userId)).thenReturn(userBalance);

        // When
        UserBalanceResponse response = useBalanceService.useBalance(request);

        // Then
        assertEquals(userId, response.getUserId());
        assertEquals(BigDecimal.valueOf(500), response.getBalance());
    }

    @Test
    @DisplayName("잔액이 부족할 때 IllegalStateException이 발생한다")
    void useBalance_ShouldThrowException_WhenBalanceIsInsufficient() {
        // Given
        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1500);
        ChargeBalanceRequest request = new ChargeBalanceRequest(userId, amount);

        UserBalance userBalance = UserBalance.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(userBalanceQueryService.getUserBalanceEntity(userId)).thenReturn(userBalance);

        doThrow(new IllegalStateException("잔액이 부족합니다."))
                .when(balanceValidationService).validateUseAmount(amount, userBalance.getBalance());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                useBalanceService.useBalance(request)
        );
        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 잔액이 없을 때 IllegalStateException이 발생한다")
    void useBalance_ShouldThrowException_WhenUserBalanceDoesNotExist() {
        // Given
        Long userId = 2L;
        BigDecimal amount = BigDecimal.valueOf(500);
        ChargeBalanceRequest request = new ChargeBalanceRequest(userId, amount);

        UserBalance userBalance = UserBalance.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();

        when(userBalanceQueryService.getUserBalanceEntity(userId)).thenReturn(userBalance);

        doThrow(new IllegalStateException("잔액이 부족합니다."))
                .when(balanceValidationService).validateUseAmount(amount, userBalance.getBalance());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                useBalanceService.useBalance(request)
        );
        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }
}

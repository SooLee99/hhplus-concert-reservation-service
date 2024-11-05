package org.example.hhplusconcertreservationservice.users.application.service.balance;

import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserBalanceQueryServiceTest {

    @InjectMocks
    private UserBalanceQueryService userBalanceQueryService;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("존재하는 사용자의 잔액 엔티티를 반환해야 한다")
    void getUserBalanceEntity_ShouldReturnUserBalance_WhenUserExists() {
        // Given
        Long userId = 1L;
        UserBalance userBalance = UserBalance.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(userBalanceRepository.findByUserId(userId)).thenReturn(Optional.of(userBalance));

        // When
        UserBalance result = userBalanceQueryService.getUserBalanceEntity(userId);

        // Then
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 경우 잔액이 0인 엔티티를 반환해야 한다")
    void getUserBalanceEntity_ShouldReturnZeroBalance_WhenUserDoesNotExist() {
        // Given
        Long userId = 1L;

        when(userBalanceRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        UserBalance result = userBalanceQueryService.getUserBalanceEntity(userId);

        // Then
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    @DisplayName("존재하는 사용자의 잔액 응답 객체를 반환해야 한다")
    void getUserBalance_ShouldReturnUserBalanceResponse_WhenUserExists() {
        // Given
        Long userId = 1L;
        UserBalance userBalance = UserBalance.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(userBalanceRepository.findByUserId(userId)).thenReturn(Optional.of(userBalance));

        // When
        UserBalanceResponse response = userBalanceQueryService.getUserBalance(userId);

        // Then
        assertEquals(userId, response.getUserId());
        assertEquals(BigDecimal.valueOf(1000), response.getBalance());
    }
}

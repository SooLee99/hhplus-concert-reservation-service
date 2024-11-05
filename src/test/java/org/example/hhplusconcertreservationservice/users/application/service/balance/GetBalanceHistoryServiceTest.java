package org.example.hhplusconcertreservationservice.users.application.service.balance;

import org.example.hhplusconcertreservationservice.users.application.dto.response.BalanceHistoryResponse;
import org.example.hhplusconcertreservationservice.users.domain.TransactionType;
import org.example.hhplusconcertreservationservice.users.domain.UserBalanceHistory;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetBalanceHistoryServiceTest {

    @InjectMocks
    private GetBalanceHistoryService getBalanceHistoryService;

    @Mock
    private UserBalanceHistoryRepository balanceHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("잔액 히스토리가 있는 경우 히스토리를 반환해야 한다")
    void getBalanceHistory_ShouldReturnHistory_WhenHistoryExists() {
        // Given
        Long userId = 1L;
        List<UserBalanceHistory> histories = Arrays.asList(
                new UserBalanceHistory(userId, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), TransactionType.CHARGE),
                new UserBalanceHistory(userId, BigDecimal.valueOf(-500), BigDecimal.valueOf(500), TransactionType.USE)
        );

        when(balanceHistoryRepository.findByUserId(userId)).thenReturn(histories);

        // When
        List<BalanceHistoryResponse> responses = getBalanceHistoryService.getBalanceHistory(userId);

        // Then
        assertEquals(2, responses.size());
        assertEquals(BigDecimal.valueOf(1000), responses.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(-500), responses.get(1).getAmount());
    }

    @Test
    @DisplayName("잔액 히스토리가 없을 경우 빈 리스트를 반환해야 한다")
    void getBalanceHistory_ShouldReturnEmptyList_WhenNoHistoryExists() {
        // Given
        Long userId = 1L;

        when(balanceHistoryRepository.findByUserId(userId)).thenReturn(Arrays.asList());

        // When
        List<BalanceHistoryResponse> responses = getBalanceHistoryService.getBalanceHistory(userId);

        // Then
        assertTrue(responses.isEmpty());
    }
}

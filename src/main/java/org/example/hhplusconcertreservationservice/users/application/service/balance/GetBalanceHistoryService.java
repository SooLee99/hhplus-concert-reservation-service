package org.example.hhplusconcertreservationservice.users.application.service.balance;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.BalanceHistoryResponse;
import org.example.hhplusconcertreservationservice.users.application.usecase.balance.GetBalanceHistoryUseCase;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetBalanceHistoryService implements GetBalanceHistoryUseCase {

    private final UserBalanceHistoryRepository balanceHistoryRepository;

    @Override
    public List<BalanceHistoryResponse> getBalanceHistory(Long userId) {
        return balanceHistoryRepository.findByUserId(userId).stream()
                .map(BalanceHistoryResponse::new)
                .collect(Collectors.toList());
    }
}
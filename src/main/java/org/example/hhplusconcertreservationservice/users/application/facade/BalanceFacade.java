package org.example.hhplusconcertreservationservice.users.application.facade;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.application.dto.response.BalanceHistoryResponse;
import org.example.hhplusconcertreservationservice.users.application.usecase.balance.ChargeBalanceUseCase;
import org.example.hhplusconcertreservationservice.users.application.usecase.balance.GetBalanceHistoryUseCase;
import org.example.hhplusconcertreservationservice.users.application.usecase.balance.UseBalanceUseCase;
import org.example.hhplusconcertreservationservice.users.application.service.balance.UserBalanceQueryService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BalanceFacade {

    private final ChargeBalanceUseCase chargeBalanceUseCase;
    private final UseBalanceUseCase useBalanceUseCase;
    private final GetBalanceHistoryUseCase getBalanceHistoryUseCase;
    private final UserBalanceQueryService userBalanceQueryService;

    public UserBalanceResponse chargeBalance(ChargeBalanceRequest request) {
        return chargeBalanceUseCase.chargeBalance(request);
    }

    public UserBalanceResponse useBalance(ChargeBalanceRequest request) {
        return useBalanceUseCase.useBalance(request);
    }

    public List<BalanceHistoryResponse> getBalanceHistory(Long userId) {
        return getBalanceHistoryUseCase.getBalanceHistory(userId);
    }

    public UserBalanceResponse getUserBalance(Long userId) {
        return userBalanceQueryService.getUserBalance(userId);
    }
}

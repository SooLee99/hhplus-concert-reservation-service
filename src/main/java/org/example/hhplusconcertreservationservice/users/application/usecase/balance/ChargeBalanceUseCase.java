package org.example.hhplusconcertreservationservice.users.application.usecase.balance;

import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.springframework.stereotype.Component;

@Component
public interface ChargeBalanceUseCase {
    UserBalanceResponse chargeBalance(ChargeBalanceRequest request);
}
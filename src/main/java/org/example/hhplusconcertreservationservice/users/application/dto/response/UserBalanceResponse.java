package org.example.hhplusconcertreservationservice.users.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponse {

    private Long userId;
    private BigDecimal balance;

    public UserBalanceResponse(UserBalance userBalance) {
        this.userId = userBalance.getUserId();
        this.balance = userBalance.getBalance();
    }
}

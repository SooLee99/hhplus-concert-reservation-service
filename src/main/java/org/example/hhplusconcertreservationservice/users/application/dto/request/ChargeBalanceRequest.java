package org.example.hhplusconcertreservationservice.users.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ChargeBalanceRequest {

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal amount;

    @Builder
    public ChargeBalanceRequest(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}

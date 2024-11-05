package org.example.hhplusconcertreservationservice.users.application.usecase.balance;
import org.example.hhplusconcertreservationservice.users.application.dto.response.BalanceHistoryResponse;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface GetBalanceHistoryUseCase {
    List<BalanceHistoryResponse> getBalanceHistory(Long userId);
}
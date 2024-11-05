package org.example.hhplusconcertreservationservice.users.application.service.balance;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.application.usecase.balance.ChargeBalanceUseCase;
import org.example.hhplusconcertreservationservice.users.domain.TransactionType;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.domain.UserBalanceHistory;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceHistoryRepository;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChargeBalanceService implements ChargeBalanceUseCase {

    private final UserBalanceHistoryRepository balanceHistoryRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final BalanceValidationService balanceValidationService;
    private final UserBalanceQueryService userBalanceQueryService;

    @Override
    @Transactional
    public UserBalanceResponse chargeBalance(ChargeBalanceRequest request) {
        // 1. 충전 금액 유효성 검사
        balanceValidationService.validateChargeAmount(request.getAmount());

        // 2. 사용자 잔액 조회 - 사용자가 없으면 0원으로 설정
        UserBalance userBalance = userBalanceQueryService.getUserBalanceEntity(request.getUserId());

        // 3. 잔액 충전
        userBalance.charge(request.getAmount());

        // 4. 변경된 잔액 저장
        userBalanceRepository.save(userBalance);

        // 5. 거래 기록 저장
        balanceHistoryRepository.save(new UserBalanceHistory(
                request.getUserId(),
                request.getAmount(),
                userBalance.getBalance(),
                TransactionType.CHARGE
        ));

        return new UserBalanceResponse(userBalance);
    }
}

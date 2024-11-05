package org.example.hhplusconcertreservationservice.users.application.service.balance;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.application.usecase.balance.UseBalanceUseCase;
import org.example.hhplusconcertreservationservice.users.domain.TransactionType;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.domain.UserBalanceHistory;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceHistoryRepository;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UseBalanceService implements UseBalanceUseCase {

    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceHistoryRepository balanceHistoryRepository;
    private final BalanceValidationService balanceValidationService;
    private final UserBalanceQueryService userBalanceQueryService;

    @Override
    @Transactional
    public UserBalanceResponse useBalance(ChargeBalanceRequest request) {
        // 1. 사용자 잔액 조회 - 사용자가 없으면 0원으로 설정
        UserBalance userBalance = userBalanceQueryService.getUserBalanceEntity(request.getUserId());

        // 2. 잔액 사용(차감) 유효성 검사
        balanceValidationService.validateUseAmount(request.getAmount(), userBalance.getBalance());

        // 3. 잔액 사용 (차감)
        userBalance.use(request.getAmount());

        // 4. 변경된 잔액 저장
        userBalanceRepository.save(userBalance);

        // 5. 거래 기록 저장
        balanceHistoryRepository.save(new UserBalanceHistory(
                request.getUserId(),
                request.getAmount().negate(),
                userBalance.getBalance(),
                TransactionType.USE
        ));

        return new UserBalanceResponse(userBalance);
    }

    /**
     * 사용자 잔액을 조회하는 메서드 (결제 시 사용)
     */
    public UserBalanceResponse getUserBalance(ChargeBalanceRequest request) {
        UserBalance userBalance = userBalanceQueryService.getUserBalanceEntity(request.getUserId());
        return new UserBalanceResponse(userBalance);
    }

    /**
     * 사용자 잔액을 충전하는 메서드
     */

    public void chargeBalance(ChargeBalanceRequest request) {
        UserBalance userBalance = userBalanceRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        userBalance.charge(request.getAmount());
        userBalanceRepository.save(userBalance);
    }
}

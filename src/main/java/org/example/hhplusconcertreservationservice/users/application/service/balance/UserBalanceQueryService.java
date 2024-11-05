package org.example.hhplusconcertreservationservice.users.application.service.balance;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserBalanceQueryService {

    private final UserBalanceRepository userBalanceRepository;

    /**
     * 사용자 ID를 기반으로 잔액 조회
     * @param userId 사용자 ID
     * @return 사용자 잔액 엔티티
     */
    @Transactional(readOnly = true)
    public UserBalance getUserBalanceEntity(Long userId) {
        return userBalanceRepository.findByUserId(userId)
                .orElse(UserBalance.builder()
                        .userId(userId)
                        .balance(BigDecimal.ZERO)
                        .build());
    }

    /**
     * 사용자 ID를 기반으로 잔액 조회 및 응답 변환
     * @param userId 사용자 ID
     * @return 사용자 잔액 응답 정보
     */
    @Transactional(readOnly = true)
    public UserBalanceResponse getUserBalance(Long userId) {
        UserBalance userBalance = getUserBalanceEntity(userId);
        return new UserBalanceResponse(userBalance);
    }
}

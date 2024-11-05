package org.example.hhplusconcertreservationservice.users;

import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.infrastructure.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class TestDataInitializer {

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Transactional
    public void initializeUserBalance(Long userId, BigDecimal initialBalance) {
        UserBalance userBalance = userBalanceRepository.findByUserId(userId)
                .orElseGet(() -> new UserBalance(userId, initialBalance));
        userBalance.setBalance(initialBalance);  // 초기 잔액으로 설정
        userBalanceRepository.save(userBalance);
    }
}

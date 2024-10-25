package org.example.hhplusconcertreservationservice.users.infrastructure;

import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 잠금 적용
    @Query("SELECT u FROM UserBalance u WHERE u.userId = :userId")
    Optional<UserBalance> findByUserId(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);
}

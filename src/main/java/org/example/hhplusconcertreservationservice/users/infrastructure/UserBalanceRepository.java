package org.example.hhplusconcertreservationservice.users.infrastructure;

import jakarta.persistence.LockModeType;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
    // 사용자 ID로 잔액 조회
    Optional<UserBalance> findByUserId(Long userId);

    // 사용자 ID로 잔액 조회 (FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ub FROM UserBalance ub WHERE ub.userId = :userId")
    Optional<UserBalance> findByUserIdForUpdate(@Param("userId") Long userId);
}
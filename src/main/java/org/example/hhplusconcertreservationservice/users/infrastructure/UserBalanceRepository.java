package org.example.hhplusconcertreservationservice.users.infrastructure;

import jakarta.persistence.QueryHint;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {

    /*@Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 잠금 적용
    @Query("SELECT u FROM UserBalance u WHERE u.userId = :userId")
    Optional<UserBalance> findByUserId(@Param("userId") Long userId);*/

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // 락 타임아웃 설정 (밀리초)
    Optional<UserBalance> findByUserId(Long userId);
}

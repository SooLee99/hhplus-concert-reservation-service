package org.example.hhplusconcertreservationservice.users.infrastructure;

import jakarta.persistence.LockModeType;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.UserBalance;
import org.example.hhplusconcertreservationservice.users.domain.UserBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserBalanceHistoryRepository extends JpaRepository<UserBalanceHistory, Long> {
    List<UserBalanceHistory> findByUserId(Long userId);

}
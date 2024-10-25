package org.example.hhplusconcertreservationservice.users.infrastructure;

import jakarta.persistence.LockModeType;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    // 특정 유저의 활성 대기열을 조회 (토큰이 활성화된 경우)
    @Query("SELECT q FROM Queue q WHERE q.userId = :userId AND q.isActive = true")
    Optional<Queue> findActiveQueueByUserId(@Param("userId") Long userId);

    // 특정 상태의 대기열을 발급 시간 순서대로 조회
    @Query("SELECT q FROM Queue q WHERE q.status IN :statuses ORDER BY q.issuedTime ASC")
    List<Queue> findAllByStatusInOrderByIssuedTimeAsc(@Param("statuses") List<QueueStatus> statuses);

    // 주어진 사용자 ID에 해당하는 대기열 삭제 (유저 자발적 이탈 또는 예약 완료 시)
    @Modifying
    @Transactional
    @Query("DELETE FROM Queue q WHERE q.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 상태가 특정 상태인 대기열을 발급 시간 순으로 조회
    @Query("SELECT q FROM Queue q WHERE q.status = :status ORDER BY q.issuedTime ASC")
    List<Queue> findAllByStatusOrderByIssuedTimeAsc(@Param("status") QueueStatus status);

    // 주어진 사용자 ID에 해당하는 대기열 조회
    @Query("SELECT q FROM Queue q WHERE q.userId = :userId")
    Optional<Queue> findByUserId(@Param("userId") Long userId);

    // 상태가 특정 상태이며 만료 시간이 현재 시각 이전인 활성 대기열 조회
    @Query("SELECT q FROM Queue q WHERE q.status = :status AND q.expirationTime < :now AND q.isActive = true")
    List<Queue> findAllByStatusAndExpirationTimeBeforeAndIsActiveTrue(@Param("status") QueueStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(q) FROM Queue q WHERE q.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<QueueStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Queue q WHERE q.userId = :userId AND q.isActive = true")
    Optional<Queue> findActiveQueueByUserIdWithLock(@Param("userId") Long userId);

    Optional<Queue> findByQueueToken(String token);
}

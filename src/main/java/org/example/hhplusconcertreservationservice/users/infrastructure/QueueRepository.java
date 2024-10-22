package org.example.hhplusconcertreservationservice.users.infrastructure;

import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    // 특정 유저의 대기열 조회 (토큰이 활성화된 경우)
    @Query("SELECT q FROM Queue q WHERE q.userId = :userId AND q.isActive = true")
    Optional<Queue> findActiveQueueByUserId(@Param("userId") Long userId);

    // 대기열 순서대로 정렬하여 대기열 리스트 조회
    @Query("SELECT q FROM Queue q WHERE q.status = :status ORDER BY q.issuedTime ASC")
    List<Queue> findAllByStatusOrderByIssuedTimeAsc(@Param("status") QueueStatus status);

    // 대기열 정보 삭제 (유저 자발적 이탈 또는 예약 완료 시)
    void deleteByUserId(Long userId);


    // 만료된 대기열을 일괄 삭제하는 메서드
    @Modifying
    @Transactional
    @Query("DELETE FROM Queue q WHERE q.expirationTime < :now")
    int deleteAllByExpirationTimeBefore(@Param("now") LocalDateTime now);

    // 특정 상태를 가진 대기열 사용자들을 발급 시간 순서로 조회
    List<Queue> findAllByStatusInOrderByIssuedTimeAsc(List<QueueStatus> statuses);


    // 주어진 사용자 ID에 해당하는 활성 대기열 찾기
    Optional<Queue> findByUserId(Long userId);
}

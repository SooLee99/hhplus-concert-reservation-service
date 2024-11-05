package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.users.application.common.QueuePositionCalculator;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final ServerLoadMonitor serverLoadMonitor;
    private final QueuePositionCalculator queuePositionCalculator;
    private final Clock clock;

    /**
     * - 현재 대기열에서 유저의 대기열 정보 조회 메서드.
     *
     * @param userId 유저 ID
     * @return 대기 순서
     */
    @Transactional(readOnly = true)
    public ApplicationQueueResponse getCurrentQueueStatus(Long userId) {
        Queue queue = queueRepository.findActiveQueueByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.QUEUE_ENTRY_NOT_FOUND.getMessage()));

        if (queue.getExpirationTime() != null && queue.getExpirationTime().isBefore(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException(ExceptionMessage.TOKEN_EXPIRED.getMessage());
        }

        // 대기열 안에 사용자들의 리스트에서 FINISHED 상태의 사용자만 계산
        List<Queue> activeUsers = queueRepository.findAllByStatusInOrderByIssuedTimeAsc(
                List.of(QueueStatus.FINISHED));

        int position = queuePositionCalculator.calculatePosition(userId);
        Duration estimatedWaitTime = queuePositionCalculator.calculateEstimatedWaitTime(userId);

        queue.updateQueuePositionAndWaitTime(position, estimatedWaitTime);

        return ApplicationQueueResponse.of(queue, serverLoadMonitor.getMaxCapacity(), activeUsers.size(), activeUsers);
    }

    /**
     * 유저의 대기열 토큰을 검증하는 메서드.
     *
     * @param token 유저의 토큰
     */
    public void validateQueueToken(String token) {
        queueRepository.findByQueueToken(token)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.TOKEN_EXPIRED.getMessage()));
    }

    /**
     * 유저의 대기열 토큰을 기반으로 유저 ID를 추출하는 메서드.
     *
     * @param token 유저의 토큰
     * @return 유저 ID
     */
    @Transactional(readOnly = true)
    public Long getUserIdFromToken(String token) {
        Queue queue = queueRepository.findByQueueToken(token)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.TOKEN_EXPIRED.getMessage()));
        return queue.getUserId();  // Queue 엔티티에서 유저 ID를 반환
    }
}

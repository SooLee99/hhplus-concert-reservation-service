package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.users.application.common.QueuePositionCalculator;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.exception.*;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
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
                .orElseThrow(QueueEntryNotFoundException::new);

        if (queue.getExpirationTime() != null && queue.getExpirationTime().isBefore(LocalDateTime.now(clock))) {
            throw new TokenExpiredException();
        }

        int position = queuePositionCalculator.calculatePosition(userId);
        Duration estimatedWaitTime = queuePositionCalculator.calculateEstimatedWaitTime(userId);

        queue.updateQueuePositionAndWaitTime(position, estimatedWaitTime);

        List<Queue> allQueues = queueRepository.findAll();
        return ApplicationQueueResponse.of(queue, serverLoadMonitor.getMaxCapacity(), allQueues.size(), allQueues);
    }

    /**
     * 만료된 대기열 삭제 (주기적으로 실행됨)
     * 매 분마다 실행하여 만료된 대기열을 삭제합니다.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void removeExpiredQueues() {
        LocalDateTime now = LocalDateTime.now(clock);
        int deletedCount = queueRepository.deleteAllByExpirationTimeBefore(now);
        log.info("삭제된 큐 개수: {}", deletedCount);

        // 만료된 큐 삭제 후, 새로 입장 완료 처리
        List<Queue> remainingQueues = queueRepository.findAll();
        remainingQueues.stream()
                .sorted(Comparator.comparingInt(Queue::getQueuePosition))
                .limit(serverLoadMonitor.getMaxCapacity() - (int) remainingQueues.stream().filter(q -> q.getStatus() == QueueStatus.FINISHED).count())
                .forEach(queue -> {
                    if (queue.getStatus() == QueueStatus.TOKEN_ISSUED) {
                        queue.updateStatus(QueueStatus.REMOVED);
                        queue.updateIsActive(false);
                        queue.updateExpirationTime(LocalDateTime.now(clock).plusMinutes(5));
                        queueRepository.save(queue);
                    }
                });

        log.info("남은 큐 개수: {}", remainingQueues.size());
        remainingQueues.forEach(queue -> log.info("Queue 정보: {}", queue));
    }

}

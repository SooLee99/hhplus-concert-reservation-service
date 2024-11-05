package org.example.hhplusconcertreservationservice.users.application.service.queue;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.users.application.common.QueuePositionCalculator;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueProcessor {
    private final QueueRepository queueRepository;
    private final QueuePositionCalculator queuePositionCalculator;
    private final ServerLoadMonitor serverLoadMonitor;
    private final QueueAsyncProcessor queueAsyncProcessor; // 비동기 처리 서비스 주입
    private final Clock clock;

    private Semaphore processingSemaphore;

    @PostConstruct
    private void init() {
        int maxCapacity = serverLoadMonitor.getMaxCapacity();
        processingSemaphore = new Semaphore(maxCapacity);
    }

    public void updateMaxCapacity(int newMaxCapacity) {
        processingSemaphore = new Semaphore(newMaxCapacity);
    }

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    public void scheduledProcessQueue() {
        removeExpiredQueues(); // 만료된 큐 제거
        processQueueInFIFO();
    }

    @Transactional
    public void removeExpiredQueues() {
        LocalDateTime now = LocalDateTime.now(clock);
        // 수정된 쿼리 조건: 상태가 FINISHED이고 expirationTime이 현재 시간 이전인 큐만 조회
        List<Queue> expiredQueues = queueRepository.findAllByStatusAndExpirationTimeBeforeAndIsActiveTrue(
                QueueStatus.FINISHED, now);

        for (Queue queue : expiredQueues) {
            queue.updateStatus(QueueStatus.REMOVED);
            queue.updateIsActive(false);
            queueRepository.save(queue);
            log.info("만료된 큐 제거: userId={}, queueId={}", queue.getUserId(), queue.getQueueId());
        }
    }

    public void processQueueInFIFO() {
        List<Queue> waitingUsers = queueRepository.findAllByStatusInOrderByIssuedTimeAsc(
                List.of(QueueStatus.WAITING, QueueStatus.TOKEN_ISSUED));

        for (Queue queue : waitingUsers) {
            int position = queuePositionCalculator.calculatePosition(queue.getUserId());
            Duration estimatedWaitTime = queuePositionCalculator.calculateEstimatedWaitTime(queue.getUserId());

            queue.updateQueuePosition(position);
            queue.updateEstimatedWaitTime(estimatedWaitTime);

            if (processingSemaphore.tryAcquire()) {
                queue.updateStatus(QueueStatus.PROCESSING);
                queue.updateActivationTime(LocalDateTime.now(clock));
                queueRepository.save(queue);

                // 비동기 서비스를 통해 사용자 처리 수행
                queueAsyncProcessor.processUserEntryAsync(queue, processingSemaphore);
            } else {
                queue.updateStatus(QueueStatus.WAITING);
                queue.updateExpirationTime(null);
                queueRepository.save(queue);
            }
        }

        logQueueProcessingInfo(processingSemaphore.availablePermits(), waitingUsers.size());
    }

    /**
     * 현재 서버 상태와 대기열 정보를 로그로 출력하는 메서드
     */
    private void logQueueProcessingInfo(int availablePermits, int queueSize) {
        int maxCapacity = serverLoadMonitor.getMaxCapacity();
        int processingCount = maxCapacity - availablePermits;

        log.info("현재 서버 최대 수용 인원: {}, 처리 중인 사용자 수: {}, 처리 가능 슬롯: {}, 대기 중인 사용자 수: {}",
                maxCapacity, processingCount, availablePermits, queueSize);
    }
}

// verifiable

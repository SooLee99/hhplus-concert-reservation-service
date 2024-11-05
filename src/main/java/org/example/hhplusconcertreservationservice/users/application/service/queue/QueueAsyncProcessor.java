package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
public class QueueAsyncProcessor {

    private final QueueRepository queueRepository;
    private final Clock clock;

    public QueueAsyncProcessor(QueueRepository queueRepository, Clock clock) {
        this.queueRepository = queueRepository;
        this.clock = clock;
    }

    @Async
    public void processUserEntryAsync(Queue queue, Semaphore processingSemaphore) {
        try {
            // 실제 비즈니스 로직 수행
            boolean isProcessed = processUserEntry(queue);

            if (isProcessed) {
                queue.updateStatus(QueueStatus.FINISHED);
                queue.updateExpirationTime(LocalDateTime.now(clock).plusMinutes(5)); // 만료 시간 5분 후로 설정
            } else {
                queue.updateStatus(QueueStatus.REJECTED);
                queue.updateExpirationTime(null);
            }

            queueRepository.save(queue);
        } catch (Exception e) {
            log.error("사용자 입장 처리 중 예외 발생: userId={}, error={}", queue.getUserId(), e.getMessage());
            queue.updateStatus(QueueStatus.REJECTED);
            queue.updateExpirationTime(null);
            queueRepository.save(queue);
        } finally {
            // 처리 완료 후 세마포어 반환
            processingSemaphore.release();
        }
    }

    private boolean processUserEntry(Queue queue) {
        try {
            // 비즈니스 로직 예시 (처리 시간 시뮬레이션)
            Thread.sleep(2000); // 2초 대기
            return true; // 처리 성공
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false; // 처리 실패
        }
    }
}

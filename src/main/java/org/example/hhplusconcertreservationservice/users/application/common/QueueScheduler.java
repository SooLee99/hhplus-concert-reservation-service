package org.example.hhplusconcertreservationservice.users.application.common;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueManager;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueService queueService;
    private final QueueManager queueManager;

    /**
     * 5분마다 만료된 대기열 정보 삭제
     */
    @Scheduled(fixedRate = 300000)
    public void cleanExpiredQueues() {
        queueService.removeExpiredQueues();
    }

    /**
     * 큐 상태를 주어진 시간에 업데이트하는 메서드
     * @param queue 큐 객체
     * @param newStatus 변경할 새로운 상태
     * @param activationTime 상태 변경이 일어날 시간
     */
    public void scheduleQueueStatusUpdate(Queue queue, QueueStatus newStatus, LocalDateTime activationTime) {
        long delay = Duration.between(LocalDateTime.now(), activationTime).toMillis();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                queueManager.updateQueueStatus(queue, newStatus);
            }
        }, delay);
    }
}

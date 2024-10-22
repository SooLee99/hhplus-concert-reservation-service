package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.users.application.common.QueuePositionCalculator;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 대기열을 FIFO 방식으로 처리하고, 서버 상태에 따라 대기열을 관리하는 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueueProcessor {

    private final QueueRepository queueRepository;
    private final QueuePositionCalculator queuePositionCalculator;
    private final Clock clock;

    /**
     * FIFO 방식으로 대기열을 처리하며 상태를 순차적으로 업데이트
     */
    @Transactional
    public void processQueueInFIFO(int maxCapacity) {
        List<Queue> waitingUsers = queueRepository.findAllByStatusInOrderByIssuedTimeAsc(List.of(QueueStatus.WAITING, QueueStatus.TOKEN_ISSUED));

        for (int i = 0; i < waitingUsers.size(); i++) {
            Queue queue = waitingUsers.get(i);
            int newPosition = i + 1;
            Duration estimatedWaitTime = queuePositionCalculator.calculateEstimatedWaitTime(queue.getUserId());

            queue.updateQueuePosition(newPosition);
            queue.updateEstimatedWaitTime(estimatedWaitTime);

            // 최대 수용 인원 내의 유저는 상태를 업데이트
            if (i < maxCapacity) {
                queue.updateStatus(QueueStatus.PROCESSING);
                queue.updateActivationTime(LocalDateTime.now(clock));
            } else {
                queue.updateStatus(QueueStatus.WAITING);
            }

            queueRepository.save(queue);
        }
    }

    /**
     * 대기열의 개별 Queue를 처리하는 메서드
     */
    private void processQueue(Queue queue, int maxCapacity) {
        QueueStatus newStatus = determineNewStatus(queue, maxCapacity);
        queue.updateStatus(newStatus);

        // 상태에 따른 만료 시간 처리
        updateExpirationTimeBasedOnStatus(queue, newStatus);

        // 상태 업데이트 후 저장
        queueRepository.save(queue);
    }

    /**
     * 상태에 따른 만료 시간 업데이트 로직 분리
     */
    private void updateExpirationTimeBasedOnStatus(Queue queue, QueueStatus newStatus) {
        if (newStatus == QueueStatus.FINISHED) {
            queue.updateExpirationTime(LocalDateTime.now(clock).plusMinutes(5));  // 만료 시간 5분으로 설정
        } else {
            queue.updateExpirationTime(null);  // 그 외 상태에서는 만료 시간 null
        }
    }

    /**
     * 대기열 처리 후 로그를 출력하는 메서드
     */
    private void logQueueProcessingInfo(int maxCapacity, int queueSize) {
        log.info("현재 서버 최대 수용 인원: {}, 대기 중인 사용자 수: {}", maxCapacity, queueSize);
    }

    /**
     * 대기열의 상태를 결정하는 메서드
     */
    public QueueStatus determineNewStatus(Queue queue, int maxCapacity) {
        if (queue.getQueuePosition() < maxCapacity) {
            return QueueStatus.FINISHED;  // 최대 인원 안에 들어온 사람들은 '입장 완료' 상태로 변경
        } else {
            return QueueStatus.WAITING;  // 나머지 사람들은 '대기 중' 상태로 변경
        }
    }
}

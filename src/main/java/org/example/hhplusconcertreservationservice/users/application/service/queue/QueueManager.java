package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.users.application.common.QueuePositionCalculator;
import org.example.hhplusconcertreservationservice.users.application.common.TokenGenerator;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.exception.ActiveTokenExistsException;
import org.example.hhplusconcertreservationservice.users.application.exception.QueueCreationFailedException;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 대기열 관리(대기열 생성, 삭제, 업데이트 등)의 역할을 담당하는 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueueManager {

    private final QueueRepository queueRepository;
    private final QueueProcessor queueProcessor;
    private final TokenGenerator tokenGenerator;
    private final QueuePositionCalculator queuePositionCalculator;
    private final Clock clock;

    /**
     * - 유저에게 대기열 토큰을 발급하고 대기열을 생성하는 메서드.
     *
     * @param userId 유저 ID
     * @param maxCapacity 최대 수용 가능 인원 수
     * @return ApplicationQueueResponse
     */
    @Transactional
    public ApplicationQueueResponse issueQueueToken(Long userId, int maxCapacity) {
        Optional<Queue> existingQueue = queueRepository.findActiveQueueByUserId(userId);
        if (existingQueue.isPresent()) {
            throw new ActiveTokenExistsException();
        }
        try {
            Queue newQueue = Queue.builder()
                    .userId(userId)
                    .queueToken(tokenGenerator.generateToken(userId))
                    .status(QueueStatus.TOKEN_ISSUED)
                    .activationTime(LocalDateTime.now(clock))
                    .expirationTime(null)
                    .build();

            queueRepository.save(newQueue);  // 먼저 저장

            int newQueuePosition = queuePositionCalculator.calculatePosition(userId);  // 위치 계산
            Duration estimatedWaitTime = queuePositionCalculator.calculateEstimatedWaitTime(userId);  // 예상 대기 시간 계산

            // 계산된 위치와 예상 대기 시간을 큐 엔티티에 설정
            newQueue.updateQueuePosition(newQueuePosition);
            newQueue.updateEstimatedWaitTime(estimatedWaitTime);

            queueRepository.save(newQueue);  // 업데이트된 큐 저장
            log.info("큐 저장 완료");

            queueProcessor.processQueueInFIFO(maxCapacity);  // FIFO 처리
            log.info("큐 처리 완료");

            List<Queue> allQueues = queueRepository.findAll();
            ApplicationQueueResponse response = ApplicationQueueResponse.of(newQueue, maxCapacity, allQueues.size(), allQueues);
            log.info("ApplicationQueueResponse: {}", response);
            return response;
        } catch (RuntimeException e) {
            log.error("대기열 생성 실패: {}", e.getMessage());
            throw new QueueCreationFailedException();
        }
    }

    /**
     * 대기열에서 유저를 제거하는 메서드
     */
    @Transactional
    public void removeUserFromQueue(Long userId) {
        queueRepository.deleteByUserId(userId);
    }

    /**
     * 대기열 상태 업데이트
     */
    @Transactional
    public void updateQueueStatus(Queue queue, QueueStatus newStatus) {
        queue.updateStatus(newStatus);
        queueRepository.save(queue);
    }
}

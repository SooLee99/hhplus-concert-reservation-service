package org.example.hhplusconcertreservationservice.users.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ApplicationQueueResponse {

    private final Long queueId;
    private final Long userId;
    private final int queuePosition;
    private final String queueToken;
    private final LocalDateTime issuedTime;
    private final QueueStatus status;
    private final boolean isActive;
    private final LocalDateTime activationTime;
    private final LocalDateTime expirationTime;
    private final Duration estimatedWaitTime;
    private final int maxCapacity;
    private final int currentQueueCount;
    private final int finishedCount;
    private final int waitingCount;
    private final int tokenIssuedCount;
    private final List<QueueInfo> queues;

    @Builder
    public ApplicationQueueResponse(Long queueId, Long userId, int queuePosition, String queueToken,
                                    LocalDateTime issuedTime, QueueStatus status, boolean isActive,
                                    LocalDateTime activationTime, LocalDateTime expirationTime, Duration estimatedWaitTime,
                                    int maxCapacity, int currentQueueCount, int finishedCount,
                                    int waitingCount, int tokenIssuedCount, List<QueueInfo> queues) {
        this.queueId = queueId;
        this.userId = userId;
        this.queuePosition = queuePosition;
        this.queueToken = queueToken;
        this.issuedTime = issuedTime;
        this.status = status;
        this.isActive = isActive;
        this.activationTime = activationTime;
        this.expirationTime = expirationTime;
        this.estimatedWaitTime = estimatedWaitTime;
        this.maxCapacity = maxCapacity;
        this.currentQueueCount = currentQueueCount;
        this.finishedCount = finishedCount;
        this.waitingCount = waitingCount;
        this.tokenIssuedCount = tokenIssuedCount;
        this.queues = queues;
    }

    public static ApplicationQueueResponse of(Queue queue, int maxCapacity, int currentQueueCount, List<Queue> allQueues) {
        // 대기열에서 FINISHED 상태인 사용자를 제외한 나머지 상태별로 사용자 수 계산
        Map<QueueStatus, Long> statusCounts = allQueues.stream()
                .filter(q -> !q.getStatus().equals(QueueStatus.FINISHED))  // FINISHED 상태 제외
                .collect(Collectors.groupingBy(Queue::getStatus, Collectors.counting()));

        // 입장이 완료된 사용자 수 계산 (FINISHED 상태만 카운트)
        int finishedCount = (int) allQueues.stream()
                .filter(q -> q.getStatus().equals(QueueStatus.FINISHED))
                .count();

        // WAITING 상태와 TOKEN_ISSUED 상태의 사용자 수 계산
        int waitingCount = statusCounts.getOrDefault(QueueStatus.WAITING, 0L).intValue();
        int tokenIssuedCount = statusCounts.getOrDefault(QueueStatus.TOKEN_ISSUED, 0L).intValue();

        // 현재 Queue의 정보를 담은 ApplicationQueueResponse 반환
        return ApplicationQueueResponse.builder()
                .queueId(queue.getQueueId())
                .userId(queue.getUserId())
                .queuePosition(queue.getQueuePosition())
                .queueToken(queue.getQueueToken())
                .issuedTime(queue.getIssuedTime())
                .status(queue.getStatus())
                .isActive(queue.isActive())
                .activationTime(queue.getActivationTime())
                .expirationTime(queue.getExpirationTime())
                .estimatedWaitTime(queue.getEstimatedWaitTime())
                .maxCapacity(maxCapacity)
                .currentQueueCount(currentQueueCount)  // 전체 대기 중인 사용자 수
                .finishedCount(finishedCount)  // 입장이 완료된 사용자 수
                .waitingCount(waitingCount)  // 대기 중인 사용자 수
                .tokenIssuedCount(tokenIssuedCount)  // 발급된 토큰 수
                .queues(allQueues.stream().map(QueueInfo::of).collect(Collectors.toList()))  // Queue의 세부 정보
                .build();
    }

    @Getter
    public static class QueueInfo {
        private Long queueId;
        private Long userId;
        private int queuePosition;
        private QueueStatus status;
        private LocalDateTime activationTime;
        private LocalDateTime expirationTime;
        private Duration estimatedWaitTime;

        // 기본 생성자 추가
        public QueueInfo() {
        }

        @Builder
        public QueueInfo(Long queueId, Long userId, int queuePosition, QueueStatus status,
                         LocalDateTime activationTime, LocalDateTime expirationTime, Duration estimatedWaitTime) {
            this.queueId = queueId;
            this.userId = userId;
            this.queuePosition = queuePosition;
            this.status = status;
            this.activationTime = activationTime;
            this.expirationTime = expirationTime;
            this.estimatedWaitTime = estimatedWaitTime;
        }

        public static QueueInfo of(Queue queue) {
            return QueueInfo.builder()
                    .queueId(queue.getQueueId())
                    .userId(queue.getUserId())
                    .queuePosition(queue.getQueuePosition())
                    .status(queue.getStatus())
                    .activationTime(queue.getActivationTime())
                    .expirationTime(queue.getExpirationTime())
                    .estimatedWaitTime(queue.getEstimatedWaitTime())
                    .build();
        }
    }
}

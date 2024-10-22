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
        Map<QueueStatus, Long> statusCounts = allQueues.stream()
                .collect(Collectors.groupingBy(Queue::getStatus, Collectors.counting()));

        int finishedCount = statusCounts.getOrDefault(QueueStatus.FINISHED, 0L).intValue();
        int waitingCount = statusCounts.getOrDefault(QueueStatus.WAITING, 0L).intValue();
        int tokenIssuedCount = statusCounts.getOrDefault(QueueStatus.TOKEN_ISSUED, 0L).intValue();

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
                .currentQueueCount(currentQueueCount)
                .finishedCount(finishedCount)
                .waitingCount(waitingCount)
                .tokenIssuedCount(tokenIssuedCount)
                .queues(allQueues.stream().map(QueueInfo::of).collect(Collectors.toList()))
                .build();
    }

    @Getter
    public static class QueueInfo {
        private final Long queueId;
        private final Long userId;
        private final int queuePosition;
        private final QueueStatus status;
        private final LocalDateTime activationTime;
        private final LocalDateTime expirationTime;
        private final Duration estimatedWaitTime;

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

package org.example.hhplusconcertreservationservice.users.interfaces.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class InterfacesQueueResponse {

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
    private final int tokenIssuedCount;
    private final int waitingCount;
    private final int finishedCount;
    private final List<ApplicationQueueResponse.QueueInfo> queues;

    // 애플리케이션 레이아웃의 QueueResponse로부터 빌드하는 생성자
    @Builder
    public InterfacesQueueResponse(ApplicationQueueResponse queueResponse) {
        this.queueId = queueResponse.getQueueId();
        this.userId = queueResponse.getUserId();
        this.queuePosition = queueResponse.getQueuePosition();
        this.queueToken = queueResponse.getQueueToken();
        this.issuedTime = queueResponse.getIssuedTime();
        this.status = queueResponse.getStatus();
        this.isActive = queueResponse.isActive();
        this.activationTime = queueResponse.getActivationTime();
        this.expirationTime = queueResponse.getExpirationTime();
        this.estimatedWaitTime = queueResponse.getEstimatedWaitTime();
        this.maxCapacity = queueResponse.getMaxCapacity();
        this.currentQueueCount = queueResponse.getCurrentQueueCount();
        this.tokenIssuedCount = queueResponse.getTokenIssuedCount();
        this.waitingCount = queueResponse.getWaitingCount();
        this.finishedCount = queueResponse.getFinishedCount();
        this.queues = queueResponse.getQueues();
    }
}
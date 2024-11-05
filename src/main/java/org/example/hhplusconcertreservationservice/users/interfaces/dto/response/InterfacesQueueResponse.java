package org.example.hhplusconcertreservationservice.users.interfaces.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterfacesQueueResponse {

    private Long queueId;
    private Long userId;
    private int queuePosition;
    private String queueToken;
    private LocalDateTime issuedTime;

    // QueueStatus가 잘 변환되도록 수정
    private QueueStatus status;

    private boolean isActive;
    private LocalDateTime activationTime;
    private LocalDateTime expirationTime;
    private Duration estimatedWaitTime;
    private int maxCapacity;
    private int currentQueueCount;
    private int tokenIssuedCount;
    private int waitingCount;
    private int finishedCount;
    private List<ApplicationQueueResponse.QueueInfo> queues;

    // 애플리케이션 레이아웃의 QueueResponse로부터 빌드하는 생성자
    @Builder
    public InterfacesQueueResponse(ApplicationQueueResponse queueResponse) {
        this.queueId = queueResponse.getQueueId();
        this.userId = queueResponse.getUserId();
        this.queuePosition = queueResponse.getQueuePosition();
        this.queueToken = queueResponse.getQueueToken();
        this.issuedTime = queueResponse.getIssuedTime();
        this.status = queueResponse.getStatus(); // QueueStatus 변환
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

package org.example.hhplusconcertreservationservice.users.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.BaseEntity;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "queues")
public class Queue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private Long queueId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "queue_position", nullable = false)
    private int queuePosition;

    @Column(name = "estimated_wait_time")
    private Duration estimatedWaitTime;   // 예상 대기 시간

    @Column(name = "queue_token", nullable = false, unique = true)
    private String queueToken;

    @Column(name = "issued_time", nullable = false)
    private LocalDateTime issuedTime;   // 대기열 발급 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private QueueStatus status;

    @Column(name = "is_active")
    private boolean isActive;               // 대기열 활성화 여부

    @Column(name = "activation_time")
    private LocalDateTime activationTime;   // 대기열 활성화 시간

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;   // 대기열 만료 시간


    @PrePersist
    protected void onCreate() {
        this.issuedTime = LocalDateTime.now();
        this.isActive = true;
    }

    @Builder
    public Queue(Long userId, Duration estimatedWaitTime, int queuePosition, String queueToken, QueueStatus status, LocalDateTime expirationTime, LocalDateTime activationTime) {
        this.userId = userId;
        this.queuePosition = queuePosition;
        this.estimatedWaitTime = estimatedWaitTime;
        this.queueToken = queueToken;
        this.status = status;
        this.expirationTime = expirationTime;
        this.activationTime = activationTime;
        this.isActive = true;  // 기본 활성화 설정
    }

    /**
     * 상태 업데이트 메서드: 대기열 상태를 업데이트합니다.
     * @param newStatus 새로운 상태
     */
    public void updateStatus(QueueStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 대기열 위치와 예상 대기 시간을 동시에 업데이트하는 메서드.
     * @param newQueuePosition 새로운 대기열 위치
     * @param newEstimatedWaitTime 새로운 예상 대기 시간
     */
    public void updateQueuePositionAndWaitTime(int newQueuePosition, Duration newEstimatedWaitTime) {
        this.queuePosition = newQueuePosition;
        this.estimatedWaitTime = newEstimatedWaitTime;
    }

    /**
     * 대기열 활성화 여부를 업데이트하는 메서드.
     * @return 만료 여부
     */
    public void updateIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * 대기열 만료 시간을 업데이트하는 메서드.
     * @param newExpirationTime 새로운 만료 시간
     */
    public void updateExpirationTime(LocalDateTime newExpirationTime) {
        this.expirationTime = newExpirationTime;
    }

    // 정적 팩토리 메서드
    public static Queue create(Long userId, String queueToken, int queuePosition, QueueStatus status,
                               LocalDateTime activationTime, Duration estimatedWaitTime, LocalDateTime expirationTime) {
        return Queue.builder()
                .userId(userId)
                .queueToken(queueToken)
                .queuePosition(queuePosition)
                .status(status)
                .activationTime(activationTime)
                .estimatedWaitTime(estimatedWaitTime)
                .expirationTime(expirationTime)
                .build();
    }

    public void updateQueuePosition(int newPosition) {
        this.queuePosition = newPosition;
    }

    public void updateEstimatedWaitTime(Duration estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }

    public void updateActivationTime(LocalDateTime activationTime) {
        this.activationTime = activationTime;
    }
}

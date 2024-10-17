package org.example.hhplusconcertreservationservice.users.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueManager;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueValidationService;
import org.example.hhplusconcertreservationservice.users.application.service.queue.ServerLoadMonitor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IssueQueueTokenUseCase {
    private final QueueManager queueManager;
    private final QueueValidationService queueValidationService;
    private final ServerLoadMonitor serverLoadMonitor;

    /**
     * [1. 토큰 발급 API]
     * - 유저에게 대기열 토큰을 발급하고 대기열 정보를 반환하는 메서드.
     *
     * @param userId 유저 ID
     * @return ApplicationQueueResponse 대기열 정보
     */
    public ApplicationQueueResponse execute(Long userId) {
        // 1. QueueValidationService를 사용해 유효성 검증을 수행
        queueValidationService.validateUserForQueueToken(userId);

        // 2. 검증 통과 후 QueueManager를 사용하여 토큰 발급
        return queueManager.issueQueueToken(userId, serverLoadMonitor.getMaxCapacity());
    }
}

package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.usecase.queue.IssueQueueTokenUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueQueueTokenService implements IssueQueueTokenUseCase {
    private final QueueManager queueManager;
    private final QueueValidationService queueValidationService;
    private final ServerLoadMonitor serverLoadMonitor;

    @Override
    public ApplicationQueueResponse execute(Long userId) {
        queueValidationService.validateUserForQueueToken(userId);
        return queueManager.issueQueueToken(userId, serverLoadMonitor.getMaxCapacity());
    }
}
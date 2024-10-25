package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.usecase.queue.CheckQueuePositionUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckQueuePositionService implements CheckQueuePositionUseCase {
    private final QueueService queueService;
    @Override
    public ApplicationQueueResponse execute(Long userId) {
        return queueService.getCurrentQueueStatus(userId);
    }
}

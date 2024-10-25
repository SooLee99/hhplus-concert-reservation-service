package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueValidationService {

    private final QueueRepository queueRepository;
    /**
     * 유저가 유효한지 확인하고, 이미 대기열에 있는지 검증하는 메서드.
     *
     * @param userId 유저 ID
     */
    public void validateUserForQueueToken(Long userId) {
        queueRepository.findActiveQueueByUserId(userId).ifPresent(queue -> {
            throw new IllegalArgumentException(ExceptionMessage.ACTIVE_TOKEN_EXISTS.getMessage());
        });
    }
}

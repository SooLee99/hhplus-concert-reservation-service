package org.example.hhplusconcertreservationservice.users.application.service.queue;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.exception.ActiveTokenExistsException;
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
        validateTokenNotIssued(userId);
        validateUserNotInQueue(userId);
    }

    /**
     * [1. 토큰 발급 API]
     * - 유저가 토큰이 이미 발급되어 있는지 확인하는 메서드.
     */
    public void validateTokenNotIssued(Long userId) {
        queueRepository.findActiveQueueByUserId(userId).ifPresent(queue -> {
            throw new ActiveTokenExistsException();
        });
    }

    /**
     * [1. 토큰 발급 API]
     * - 유저가 이미 대기열에 있는지 확인하는 메서드.
     *
     * @param userId 유저 ID
     */
    public void validateUserNotInQueue(Long userId) {
        queueRepository.findActiveQueueByUserId(userId).ifPresent(queue -> {
            throw new ActiveTokenExistsException();
        });
    }
}

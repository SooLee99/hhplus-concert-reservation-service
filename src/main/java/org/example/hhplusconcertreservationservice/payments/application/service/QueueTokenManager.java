package org.example.hhplusconcertreservationservice.payments.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueManager;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueTokenManager {

    private final QueueService queueService;
    private final QueueManager queueManager;

    public Long validateToken(String token) {
        return queueService.getUserIdFromToken(token);
    }

    public void expireToken(Long userId) {
        try {
            queueManager.removeUserFromQueue(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("결제는 완료되었으나 대기열 토큰 만료에 실패했습니다.", e);
        }
    }
}

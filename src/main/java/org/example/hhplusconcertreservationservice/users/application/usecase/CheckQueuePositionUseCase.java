package org.example.hhplusconcertreservationservice.users.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckQueuePositionUseCase {

    private final QueueService queueService;

    /**
     * [2. 대기열 조회 API]
     * - 유저의 현재 대기열 위치와 예상 대기 시간을 조회하는 메서드.
     *
     * @param userId 유저 ID
     * @return QueueResponse 대기열 정보
     */
    public ApplicationQueueResponse execute(Long userId) {
        // 1. QueueService에서 유저의 대기열 위치 및 예상 대기 시간을 조회
        return queueService.getCurrentQueueStatus(userId);
    }
}

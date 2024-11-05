package org.example.hhplusconcertreservationservice.users.application.usecase.queue;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueManager;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueValidationService;
import org.example.hhplusconcertreservationservice.users.application.service.queue.ServerLoadMonitor;
import org.springframework.stereotype.Component;

// 시스템에서 사용자가 요청하는 특정 시나리오를 처리하는 비즈니스 로직을 담당하는 클래스
public interface IssueQueueTokenUseCase {
    ApplicationQueueResponse execute(Long userId);
}

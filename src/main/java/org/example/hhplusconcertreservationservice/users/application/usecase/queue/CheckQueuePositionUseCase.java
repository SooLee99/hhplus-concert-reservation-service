package org.example.hhplusconcertreservationservice.users.application.usecase.queue;

import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.springframework.stereotype.Component;

public interface CheckQueuePositionUseCase {
    ApplicationQueueResponse execute(Long userId);
}

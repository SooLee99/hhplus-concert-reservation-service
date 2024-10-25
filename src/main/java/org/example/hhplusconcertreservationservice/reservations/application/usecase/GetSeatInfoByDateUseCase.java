package org.example.hhplusconcertreservationservice.reservations.application.usecase;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

public interface GetSeatInfoByDateUseCase {

    @Transactional(readOnly = true)
    Map<String, Object> getSeatInfoByDateTime(LocalDateTime date, String token);
}
package org.example.hhplusconcertreservationservice.reservations.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

public interface GetAvailableDatesUseCase {
    List<LocalDateTime> getAvailableDates();
}
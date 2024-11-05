package org.example.hhplusconcertreservationservice.reservations.application.usecase;

import java.util.List;
import java.util.Map;

public interface GetAvailableDatesUseCase {
    List<Map<String, Object>> getAvailableDates(String token);
}
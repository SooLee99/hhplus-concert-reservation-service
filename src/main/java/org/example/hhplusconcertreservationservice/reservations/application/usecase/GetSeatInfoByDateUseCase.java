package org.example.hhplusconcertreservationservice.reservations.application.usecase;

import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface GetSeatInfoByDateUseCase {
    List<SeatInfoResponse> getSeatInfoByDate(LocalDateTime date);
}
package org.example.hhplusconcertreservationservice.reservations.application.usecase;

import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.CreateReservationResponse;


// 좌석 예약 유스케이스
public interface CreateReservationUseCase {
    CreateReservationResponse createReservation(CreateReservationRequest request);
}

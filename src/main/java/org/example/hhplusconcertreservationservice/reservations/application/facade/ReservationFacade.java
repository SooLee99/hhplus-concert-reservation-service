package org.example.hhplusconcertreservationservice.reservations.application.facade;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.application.dto.request.SeatInfoRequest;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetAvailableDatesUseCase;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetSeatInfoByDateUseCase;
import org.example.hhplusconcertreservationservice.reservations.application.service.CreateReservationService;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.TokenRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.CreateReservationResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final GetAvailableDatesUseCase getAvailableDatesUseCase;
    private final GetSeatInfoByDateUseCase getSeatInfoByDateUseCase;
    private final CreateReservationService createReservationService;

    public List<Map<String, Object>> getAvailableDates(TokenRequest tokenRequest) {
        return getAvailableDatesUseCase.getAvailableDates(tokenRequest.getToken());
    }

    public Map<String, Object> getSeatInfoByDate(SeatInfoRequest request) {
        return getSeatInfoByDateUseCase.getSeatInfoByDateTime(request.getDate(), request.getToken());
    }

    public CreateReservationResponse createReservation(CreateReservationRequest request) {
        return createReservationService.createReservation(request);
    }
}

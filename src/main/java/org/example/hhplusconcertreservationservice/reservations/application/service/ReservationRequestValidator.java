package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationRequestValidator {
    public void validate(CreateReservationRequest request) {
        if (request.getToken() == null || request.getSeatId() == null) {
            throw new IllegalArgumentException(ExceptionMessage.MISSING_REQUIRED_FIELDS.getMessage());
        }
    }
}

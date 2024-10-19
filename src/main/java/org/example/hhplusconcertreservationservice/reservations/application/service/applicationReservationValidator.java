package org.example.hhplusconcertreservationservice.reservations.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class applicationReservationValidator {
    public void validate(CreateReservationRequest request) {
        if (request.getUserId() == null || request.getSeatId() == null || request.getScheduleId() == null) {
            throw new IllegalArgumentException("필수 입력 값이 누락되었습니다.");
        }
    }
}

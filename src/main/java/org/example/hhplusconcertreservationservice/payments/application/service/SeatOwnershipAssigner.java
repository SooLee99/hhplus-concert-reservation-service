package org.example.hhplusconcertreservationservice.payments.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.application.service.SeatOwnershipService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatOwnershipAssigner {

    private final SeatOwnershipService seatOwnershipService;

    public void assignSeatToUser(Long userId, Long seatId) {
        try {
            seatOwnershipService.assignSeatToUser(userId, seatId);
        } catch (Exception e) {
            throw new IllegalArgumentException("결제는 완료되었으나 좌석 소유권 배정에 실패했습니다.", e);
        }
    }
}

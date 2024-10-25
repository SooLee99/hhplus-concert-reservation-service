package org.example.hhplusconcertreservationservice.reservations.interfaces;

import lombok.AllArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.application.dto.request.SeatInfoRequest;
import org.example.hhplusconcertreservationservice.reservations.application.facade.ReservationFacade;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.TokenRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.CreateReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationFacade reservationFacade;

    // 예약 가능 날짜 조회 API
    @PostMapping("/available-dates")
    public ResponseEntity<List<Map<String, Object>>> getAvailableDates(@RequestBody TokenRequest tokenRequest) {
        return ResponseEntity.ok(reservationFacade.getAvailableDates(tokenRequest));
    }

    // 날짜별 좌석 정보 조회 API
    @PostMapping("/seats")
    public Map<String, Object> getSeatInfoByDate(@RequestBody SeatInfoRequest request) {
        return reservationFacade.getSeatInfoByDate(request);
    }

    // 좌석 예약 요청 처리 API
    @PostMapping("/seats/reserve")
    public ResponseEntity<CreateReservationResponse> reserveSeat(
            @RequestBody CreateReservationRequest request) {
        CreateReservationResponse response = reservationFacade.createReservation(request);
        return ResponseEntity.ok(response);
    }

}

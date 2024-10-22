package org.example.hhplusconcertreservationservice.reservations.interfaces;

import lombok.AllArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.application.service.CreateReservationService;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetAvailableDatesUseCase;
import org.example.hhplusconcertreservationservice.reservations.application.usecase.GetSeatInfoByDateUseCase;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request.CreateReservationRequest;
import org.example.hhplusconcertreservationservice.reservations.interfaces.dto.response.SeatInfoResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final GetAvailableDatesUseCase getAvailableDatesUseCase;
    private final GetSeatInfoByDateUseCase getSeatInfoByDateUseCase;
    private final CreateReservationService createReservationService;

    // 예약 가능 날짜 조회 API
    @GetMapping("/available-dates")
    public ResponseEntity<List<LocalDateTime>> getAvailableDates() {
        return ResponseEntity.ok(getAvailableDatesUseCase.getAvailableDates());
    }

    // 날짜별 좌석 정보 조회 API
    @GetMapping("/seats")
    public List<SeatInfoResponse> getSeatInfoByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date) {
        return getSeatInfoByDateUseCase.getSeatInfoByDate(date);
    }

    // 좌석 예약 요청 처리 API
    @PostMapping("/seats/{seatId}")
    public ResponseEntity<Void> reserveSeat(
            @PathVariable Long seatId,
            @RequestBody CreateReservationRequest request) {
        createReservationService.createReservation(request);
        return ResponseEntity.ok().build();
    }
}
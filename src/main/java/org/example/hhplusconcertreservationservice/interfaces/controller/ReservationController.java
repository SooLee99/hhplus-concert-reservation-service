package org.example.hhplusconcertreservationservice.interfaces.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/concerts")
public class ReservationController {

    @GetMapping("/available-dates")
    public Map<String, Object> getAvailableDates() {
        Map<String, Object> response = new HashMap<>();
        response.put("available_dates", Arrays.asList("2024-10-11", "2024-10-12", "2024-10-13"));
        return response;
    }

    @GetMapping("/{concertId}/seats")
    public Map<String, Object> getSeatsByDate(@PathVariable String concertId, @RequestParam String date) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> seats = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            Map<String, Object> seat = new HashMap<>();
            seat.put("seat_number", i);
            seat.put("is_reserved", false);
            seats.add(seat);
        }

        response.put("date", date);
        response.put("seats", seats);
        return response;
    }

    @PostMapping("/{concertId}/reservations")
    public Map<String, Object> reserveSeat(@PathVariable String concertId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "임시 예약 성공");
        response.put("reservation_id", "mock-reservation-id-123");
        response.put("expiration_time", "2024-10-11T15:30:00Z");
        return response;
    }
}

package org.example.hhplusconcertreservationservice.interfaces.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/concerts/{concertId}/reservations/{reservationId}/payments")
public class PaymentController {

    @PostMapping
    public Map<String, Object> makePayment(@PathVariable String concertId, @PathVariable String reservationId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "결제 성공");
        response.put("seat_number", 1);
        response.put("date", "2024-10-11");
        response.put("user_id", "mock-user-id-123");
        return response;
    }
}

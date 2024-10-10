package org.example.hhplusconcertreservationservice.interfaces.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    @PostMapping("/recharge")
    public Map<String, Object> rechargeBalance(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "충전 성공");
        response.put("current_balance", 10000);
        return response;
    }

    @GetMapping
    public Map<String, Object> getCurrentBalance() {
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", "mock-user-id-123");
        response.put("current_balance", 10000);
        return response;
    }
}
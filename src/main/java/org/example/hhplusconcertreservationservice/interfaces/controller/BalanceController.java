package org.example.hhplusconcertreservationservice.interfaces.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/users/{userId}/balance")
public class BalanceController {

    @PostMapping
    public Map<String, Object> rechargeBalance(@PathVariable String userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "충전 성공");
        response.put("current_balance", 10000);
        return response;
    }

    @GetMapping
    public Map<String, Object> getCurrentBalance(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);
        response.put("current_balance", 10000);
        return response;
    }
}

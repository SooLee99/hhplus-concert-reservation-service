package org.example.hhplusconcertreservationservice.interfaces.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/token")
    public Map<String, Object> issueToken(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", "mock-token-12345");
        response.put("queue_position", 1);
        response.put("estimated_wait_time", 30);
        return response;
    }
}
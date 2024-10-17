package org.example.hhplusconcertreservationservice.users.interfaces;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.facade.QueueFacade;
import org.example.hhplusconcertreservationservice.users.interfaces.dto.response.InterfacesQueueResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueFacade queueFacade;

    /**
     * 1. 대기열 토큰을 발급하는 API.
     *
     * @param userId 유저 ID
     * @return 대기열 토큰 발급 응답
     */
    @PostMapping("/issue-token")
    public ResponseEntity<InterfacesQueueResponse> issueToken(@RequestParam Long userId) {
        return ResponseEntity.ok(queueFacade.issueToken(userId));
    }

    @GetMapping("/queue-status")
    public ApplicationQueueResponse getQueueStatus(@RequestParam Long userId) {
        return queueFacade.getQueueStatus(userId);
    }
}

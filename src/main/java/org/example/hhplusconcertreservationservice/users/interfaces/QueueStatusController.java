package org.example.hhplusconcertreservationservice.users.interfaces;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * [2. 대기열 조회 API - 폴링용 API]
 * 유저의 현재 대기열 위치를 조회하는 메서드.
 * - 폴링용 API로, 대기열 상태를 조회하는 페이지를 렌더링합니다.
 * - userId를 파라미터로 받아, 해당 유저의 대기열 상태를 조회합니다.
 */
@Controller
public class QueueStatusController {
    @GetMapping("/queue-status")
    public String getQueueStatus(@RequestParam("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "queue-status";
    }
}
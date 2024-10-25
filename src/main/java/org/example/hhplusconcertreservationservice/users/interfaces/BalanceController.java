package org.example.hhplusconcertreservationservice.users.interfaces;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.example.hhplusconcertreservationservice.users.application.dto.response.BalanceHistoryResponse;
import org.example.hhplusconcertreservationservice.users.application.facade.BalanceFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balance")
public class BalanceController {

    private final BalanceFacade balanceFacade;

    // 잔액 충전 API
    @PostMapping("/charge")
    public ResponseEntity<UserBalanceResponse> chargeBalance(@RequestBody ChargeBalanceRequest request) {
        UserBalanceResponse response = balanceFacade.chargeBalance(request);
        return ResponseEntity.ok(response);
    }

    // 잔액 사용 API
    @PostMapping("/use")
    public ResponseEntity<UserBalanceResponse> useBalance(@RequestBody ChargeBalanceRequest request) {
        UserBalanceResponse response = balanceFacade.useBalance(request);
        return ResponseEntity.ok(response);
    }

    // 잔액 거래 내역 조회 API
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BalanceHistoryResponse>> getBalanceHistory(@PathVariable("userId") Long userId) {
        List<BalanceHistoryResponse> response = balanceFacade.getBalanceHistory(userId);
        return ResponseEntity.ok(response);
    }

    // 사용자 잔액 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<UserBalanceResponse> getUserBalance(@PathVariable("userId") Long userId) {
        UserBalanceResponse response = balanceFacade.getUserBalance(userId);
        return ResponseEntity.ok(response);
    }
}

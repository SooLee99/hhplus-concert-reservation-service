package org.example.hhplusconcertreservationservice.payments.interfaces;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.example.hhplusconcertreservationservice.payments.application.dto.response.PaymentResponse;
import org.example.hhplusconcertreservationservice.payments.application.facade.PaymentFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    // 결제 처리 API
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentFacade.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(@PathVariable("userId") Long userId) {
        List<PaymentResponse> paymentResponses = paymentFacade.getPaymentsByUserId(userId);
        return ResponseEntity.ok(paymentResponses);
    }
}

package org.example.hhplusconcertreservationservice.payments.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.example.hhplusconcertreservationservice.payments.domain.Payment;
import org.example.hhplusconcertreservationservice.payments.domain.PaymentStatus;
import org.example.hhplusconcertreservationservice.payments.infrastructure.PaymentRepository;
import org.example.hhplusconcertreservationservice.seats.domain.Seat;
import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatRepository;
import org.example.hhplusconcertreservationservice.seats.infrastructure.SeatTypeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentCreator {

    private final PaymentRepository paymentRepository;
    private final SeatRepository seatRepository;
    private final SeatTypeRepository seatTypeRepository;

    public BigDecimal determineAmount(PaymentRequest request) {
        // 1. 좌석 ID로 좌석 조회
        Seat seat = seatRepository.findBySeatId(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));

        // 2. 좌석 유형 ID로 좌석 유형 조회
        SeatType seatType = seatTypeRepository.findBySeatTypeId(seat.getSeatTypeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석 유형을 찾을 수 없습니다."));

        // 3. 좌석 유형의 가격 반환
        return seatType.getPrice();
    }

    public Payment createPendingPayment(PaymentRequest request, BigDecimal amount, Long userId) {
        Payment payment = Payment.builder()
                .userId(userId)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        return payment;
    }

    public void handlePaymentSuccess(Payment payment) {
        try {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw new IllegalArgumentException("결제 처리 중 오류가 발생했습니다.", e);
        }
    }
}

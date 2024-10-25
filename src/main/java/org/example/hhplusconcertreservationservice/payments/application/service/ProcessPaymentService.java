package org.example.hhplusconcertreservationservice.payments.application.service;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.payments.application.dto.request.PaymentRequest;
import org.example.hhplusconcertreservationservice.payments.application.dto.response.PaymentResponse;
import org.example.hhplusconcertreservationservice.payments.application.usecase.ProcessPaymentUseCase;
import org.example.hhplusconcertreservationservice.payments.domain.Payment;
import org.example.hhplusconcertreservationservice.payments.infrastructure.PaymentRepository;
import org.example.hhplusconcertreservationservice.reservations.application.service.SeatOwnershipService;
import org.example.hhplusconcertreservationservice.users.application.service.balance.UseBalanceService;
import org.example.hhplusconcertreservationservice.users.application.dto.request.ChargeBalanceRequest;
import org.example.hhplusconcertreservationservice.users.application.dto.response.UserBalanceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessPaymentService implements ProcessPaymentUseCase {

    private final PaymentValidator paymentValidator;
    private final PaymentCreator paymentCreator;
    private final SeatOwnershipAssigner seatOwnershipAssigner;
    private final QueueTokenManager queueTokenManager;
    private final PaymentResponseFactory paymentResponseFactory;
    private final UseBalanceService useBalanceService;
    private final SeatOwnershipService seatOwnershipService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        if (request == null || request.getToken() == null) {
            throw new IllegalArgumentException("결제 요청에 필수 정보가 없습니다.");
        }

        // 1. 결제 요청에 대한 유효성 검사
        paymentValidator.validatePaymentRequest(request);

        // 2. 대기열 토큰을 검증하여 유저가 대기열에 포함되어 있는지 확인
        Long userId = queueTokenManager.validateToken(request.getToken());
        if (userId == null) {
            throw new IllegalArgumentException("유효하지 않은 대기열 토큰입니다.");
        }
        // 3. 좌석 예약 여부 확인
        seatOwnershipService.findReservationForSeat(userId, request.getSeatId());

        // 4. 결제 금액 확인
        BigDecimal amount = paymentCreator.determineAmount(request);

        // 5. 결제 금액에 대한 유효성 검사
        paymentValidator.validateAmount(amount);

        // 6. 사용자의 잔액이 결제 금액 이상인지 확인
        checkUserBalance(userId, amount);

        // 7. 결제 기록 생성 및 저장
        Payment payment = paymentCreator.createPendingPayment(request, amount, userId);

        // 8. 결제 성공 처리
        paymentCreator.handlePaymentSuccess(payment);

        // 9. 결제 성공 후 사용자 잔액 차감
        useUserBalance(userId, amount);

        // 10. 좌석 소유권 배정
        seatOwnershipAssigner.assignSeatToUser(userId, request.getSeatId());

        // 11. 대기열 토큰 만료 처리
        queueTokenManager.expireToken(userId);

        // 12. 결제 응답 생성
        return paymentResponseFactory.createResponse(payment);
    }

    /**
     * 사용자의 잔액이 결제 금액 이상인지 확인하는 메서드
     */
    private void checkUserBalance(Long userId, BigDecimal amount) {
        UserBalanceResponse userBalance = useBalanceService.getUserBalance(new ChargeBalanceRequest(userId, amount));

        if (userBalance == null || userBalance.getBalance() == null) {
            throw new IllegalArgumentException("잔액 정보를 확인할 수 없습니다.");
        }

        if (userBalance.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }


    /**
     * 결제가 완료되면 사용자 잔액을 차감하는 메서드
     */
    private void useUserBalance(Long userId, BigDecimal amount) {
        ChargeBalanceRequest useBalanceRequest = new ChargeBalanceRequest(userId, amount);
        useBalanceService.useBalance(useBalanceRequest);
    }

    // 유저 아이디로 결제 내역 조회
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(this::convertToPaymentResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse convertToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getPaymentStatus().name(),
                payment.getPaymentTime()
        );
    }
}

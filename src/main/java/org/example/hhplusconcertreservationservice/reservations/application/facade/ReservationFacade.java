package org.example.hhplusconcertreservationservice.reservations.application.facade;

import org.example.hhplusconcertreservationservice.payments.application.service.PaymentService;
import org.example.hhplusconcertreservationservice.reservations.application.service.ReservationService;
import org.example.hhplusconcertreservationservice.users.application.service.queue.QueueService;
import org.springframework.stereotype.Component;

@Component
public class ReservationFacade {

    private final QueueService queueService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationFacade(QueueService queueService, ReservationService reservationService, PaymentService paymentService) {
        this.queueService = queueService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    // 전체 예약 프로세스 처리
    public void makeReservation(/* 필요한 매개변수 */) {
        // TODO: 대기열 확인
        // TODO: 좌석 예약
        // TODO: 결제 처리
    }
}
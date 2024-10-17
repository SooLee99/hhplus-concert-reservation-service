package org.example.hhplusconcertreservationservice.payments.infrastructure;

import org.example.hhplusconcertreservationservice.payments.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 결제 상태로 결제 조회
    List<Payment> findByPaymentStatus(String paymentStatus);
}
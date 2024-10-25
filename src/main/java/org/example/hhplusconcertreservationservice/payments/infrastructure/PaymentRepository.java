package org.example.hhplusconcertreservationservice.payments.infrastructure;

import org.example.hhplusconcertreservationservice.payments.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 유저 아이디로 결제 내역 조회
    List<Payment> findByUserId(Long userId);
}
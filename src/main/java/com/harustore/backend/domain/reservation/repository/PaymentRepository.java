package com.harustore.backend.domain.reservation.repository;

import com.harustore.backend.domain.reservation.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // TODO 1. [결제 기록 조회] 특정 예약 ID(reservationId)로 결제 정보 단건 조회
    Optional<Payment> findByReservationId(Long reservationId);

}

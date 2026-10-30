package com.harustore.backend.domain.reservation.repository;

import com.harustore.backend.domain.reservation.entity.Reservation;
import com.harustore.backend.domain.reservation.entity.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // TODO 1. [예약 번호 조회] 고유 주문번호(reservationNumber)로 예약 단건 조회
    Optional<Reservation> findByReservationNumber(String reservationNumber);

    // TODO 2. [동시성 제어 락] 수량 변경/취소/현장 수령 시 데이터 정합성을 위한 비관적 락(PESSIMISTIC_WRITE) 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id = :id")
    Optional<Reservation> findByIdWithLock(@Param("id") Long id);

    // TODO 3. [유효 예약 및 재구매 방지 검증] 특정 구매자가 특정 공구에 특정 상태(RESERVED, PICKED_UP 등)로 존재하는지 확인
    boolean existsByBuyerIdAndGroupBuyIdAndStatus(Long buyerId, Long groupBuyId, ReservationStatus status);

    // TODO 4. [구매자용] 본인의 전체 예약 내역 최신순 페이징 조회 (findByBuyerId...)
    Page<Reservation> findByBuyerId(Long buyerId, Pageable pageable);

    // TODO 5. [판매자용] 본인 상점의 전체 예약 목록 최신순 페이징 조회 (findByGroupBuyStoreId...)
    Page<Reservation> findByGroupBuyStoreId(Long storeId, Pageable pageable);

    // TODO 6. [판매자용] 본인 상점의 특정 상태별(RESERVED, PICKED_UP 등) 예약 목록 페이징 조회
    Page<Reservation> findByGroupBuyStoreIdAndStatus(Long storeId, ReservationStatus status, Pageable pageable);
}

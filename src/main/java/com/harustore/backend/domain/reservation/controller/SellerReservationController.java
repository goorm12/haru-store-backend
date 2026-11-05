package com.harustore.backend.domain.reservation.controller;

import com.harustore.backend.domain.reservation.service.SellerReservationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [판매자용 예약 관리 컨트롤러 (Seller)]
 * 기본 URL: /api/v1/seller
 */
@RestController
@RequestMapping("/api/v1/seller")
public class SellerReservationController {

    private final SellerReservationService sellerReservationService;

    public SellerReservationController(SellerReservationService sellerReservationService) {
        this.sellerReservationService = sellerReservationService;
    }

    // =========================================================================
    // API 1. [판매자 상점별 예약 목록 페이징 조회 (SELL-01)]
    // - HTTP 메서드 및 경로: GET /api/v1/seller/stores/{storeId}/reservations
    // - 설명: 사장님이 본인 상점에 들어온 예약 내역을 최신순 또는 상태별로 페이징 조회합니다.
    // - 추천 메서드명: getStoreReservations
    // =========================================================================



    // =========================================================================
    // API 2. [판매자 예약 개별 사유 취소 (SELL-02)]
    // - HTTP 메서드 및 경로: POST /api/v1/seller/reservations/{reservationId}/cancel
    // - 설명: 사장님이 사유를 입력하고 예약을 개별 취소합니다. (당일에도 관리자 승인 없이 즉시 가능)
    // - 추천 메서드명: cancelReservation
    // =========================================================================



    // =========================================================================
    // API 3. [현장 수령(픽업) 완료 처리 (SELL-04)]
    // - HTTP 메서드 및 경로: POST /api/v1/seller/reservations/{reservationId}/pickup
    // - 설명: 사장님이 매장에서 손님에게 상품을 전달하고 '수령 완료' 처리합니다.
    // - 추천 메서드명: completePickup
    // =========================================================================



    // =========================================================================
    // API 4. [미수령(노쇼) 처리 (SELL-05)]
    // - HTTP 메서드 및 경로: POST /api/v1/seller/reservations/{reservationId}/unclaimed
    // - 설명: 픽업일이 지났는데도 손님이 오지 않았음을 사장님이 확인 처리합니다.
    // - 추천 메서드명: markUnclaimed
    // =========================================================================



    // =========================================================================
    // API 5. [현장 결제 등록 및 처리 (SELL-03)]
    // - HTTP 메서드 및 경로: POST /api/v1/seller/reservations/{reservationId}/payments
    // - 설명: 손님이 매장에서 결제한 내역(현금, 카드, 계좌이체 등)을 사장님이 수기 등록합니다.
    // - 추천 메서드명: recordPayment
    // =========================================================================



    // =========================================================================
    // API 6. [예약 상태 오기록 정정 (SELL-06)]
    // - HTTP 메서드 및 경로: PATCH /api/v1/seller/reservations/{reservationId}/status
    // - 설명: 사장님이 실수로 잘못 누른 픽업 완료나 미수령 상태를 사유와 함께 정정합니다.
    // - 추천 메서드명: correctReservationStatus
    // =========================================================================



    // =========================================================================
    // API 7. [현장 결제 오기록 정정 (SELL-06)]
    // - HTTP 메서드 및 경로: PATCH /api/v1/seller/reservations/{reservationId}/payments
    // - 설명: 사장님이 실수로 잘못 입력한 결제 수단, 금액, 결제 상태를 사유와 함께 정정합니다.
    // - 추천 메서드명: correctPayment
    // =========================================================================

}

package com.harustore.backend.domain.reservation.entity;

/**
 * 예약 진행 상태 Enum
 */
public enum ReservationStatus {

    /**
     * 예약 완료 (접수 즉시 확정 상태)
     */
    RESERVED,

    /**
     * 현장 수령(픽업) 완료 (판매자가 상품 전달 확인)
     */
    PICKED_UP,

    /**
     * 미수령 (픽업일 경과 후 판매자가 미수령 처리)
     */
    UNCLAIMED,

    /**
     * 취소됨 (구매자 직접 취소 또는 판매자 관리 취소)
     */
    CANCELED
}

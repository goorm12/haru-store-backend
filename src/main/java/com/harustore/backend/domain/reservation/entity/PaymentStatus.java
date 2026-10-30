package com.harustore.backend.domain.reservation.entity;

/**
 * 현장 결제 상태 Enum
 */
public enum PaymentStatus {

    /**
     * 결제 대기 (미결제 상태)
     */
    UNPAID,

    /**
     * 현장 결제 완료
     */
    PAID,

    /**
     * 결제 취소 / 환불
     */
    CANCELED
}

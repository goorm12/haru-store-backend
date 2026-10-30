package com.harustore.backend.domain.reservation.entity;

/**
 * 현장 결제 수단 Enum
 */
public enum PaymentMethod {

    /**
     * 현금 결제
     */
    CASH,

    /**
     * 신용/체크카드 결제
     */
    CARD,

    /**
     * 계좌이체
     */
    TRANSFER,

    /**
     * 기타 결제 수단
     */
    OTHER
}

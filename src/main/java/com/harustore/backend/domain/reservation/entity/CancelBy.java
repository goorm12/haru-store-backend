package com.harustore.backend.domain.reservation.entity;

/**
 * 예약 취소 주체 Enum
 */
public enum CancelBy {

    /**
     * 구매자 직접 취소 (픽업 시작일 전날 23:59:59까지 가능)
     */
    BUYER,

    /**
     * 판매자 관리 취소 (당일 사유 취소 등 관리자 승인 없이 즉시 처리)
     */
    SELLER,

    /**
     * 플랫폼 관리자 취소 (필요 시 운영 이슈 대응)
     */
    ADMIN
}

package com.harustore.backend.domain.groupbuy.entity;

/**
 * 공동구매(GroupBuy)의 진행 상태를 관리하는 Enum
 * - DRAFT: 사장님이 작성 중인 임시저장 상태 (구매자에게 미노출)
 * - OPEN: 접수 진행 중인 상태 (구매자 예약 가능)
 * - CLOSED: 사장님이 수동으로 접수를 마감했거나 조기 완판된 상태 (신규 예약 차단, 기존 예약 유지)
 * - CANCELLED: 사장님의 부득이한 사정으로 공구 전체가 취소된 상태 (유효 예약 일괄 취소)
 */
public enum GroupBuyStatus {
    DRAFT,
    OPEN,
    CLOSED,
    CANCELLED
}

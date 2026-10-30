package com.harustore.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 프로젝트 전역에서 사용할 에러 코드와 기본 메시지, 그리고 HTTP 상태 코드를 관리하는 Enum
 * [설계 원칙]
 * 1. httpStatus: 브라우저 및 클라이언트 표준 HTTP 상태 코드 (400, 404, 500 등)
 * 2. code: 프론트엔드가 프로그래밍 방식(if문)으로 특정 에러를 식별하기 위한 고유 비즈니스 에러 코드
 * 3. message: 사용자에게 화면이나 알림으로 노출할 기본 안내 문구
 */
public enum ErrorCode {

    // ==========================================
    // 1. 공통 시스템 및 입력값 에러 (C: Common)
    // ==========================================
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 대상을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "접근 권한이 없습니다."),

    // ==========================================
    // 2. 도메인별 에러 (M: Member, S: Store, G: GroupBuy, R: Reservation)
    // ==========================================
    // 회원(Member)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회원입니다."),

    // 상점(Store)
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "존재하지 않는 상점입니다."),

    // 공구(GroupBuy)
    GROUP_BUY_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "존재하지 않는 공구입니다."),
    GROUP_BUY_NOT_OPEN(HttpStatus.BAD_REQUEST, "G002", "현재 접수 중(모집 중)인 공구가 아닙니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "G003", "공구 준비 수량이 모두 소진되었거나 재고가 부족합니다."),
    EXCEED_MAX_LIMIT_PER_USER(HttpStatus.BAD_REQUEST, "G004", "1인당 최대 구매 제한 수량을 초과했습니다."),

    // 예약(Reservation)
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 예약입니다."),
    DUPLICATE_RESERVATION(HttpStatus.BAD_REQUEST, "R002", "해당 공구에 이미 진행 중인 예약이 존재합니다."),
    ALREADY_PICKED_UP(HttpStatus.BAD_REQUEST, "R003", "이미 수령 완료된 공구는 재구매할 수 없습니다."),
    INVALID_RESERVATION_STATUS(HttpStatus.BAD_REQUEST, "R004", "현재 상태에서는 처리할 수 없는 예약입니다."),
    CANCEL_RESTRICTED(HttpStatus.BAD_REQUEST, "R005", "직접 취소가 제한된 공구 품목입니다."),
    CANCEL_DEADLINE_EXCEEDED(HttpStatus.BAD_REQUEST, "R006", "픽업 시작일 당일부터는 직접 취소가 불가하며 판매자에게 문의해야 합니다."),
    QUANTITY_REDUCE_DEADLINE_EXCEEDED(HttpStatus.BAD_REQUEST, "R007", "픽업 시작일 당일부터는 직접 수량을 줄일 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

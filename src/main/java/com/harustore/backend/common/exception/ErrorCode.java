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
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "접근 권한이 없습니다.");

    // ==========================================
    // 2. 도메인별 에러 확장 예시 (각 도메인 구현 시 추가)
    // ==========================================
    // 회원(Member): M001, M002 ...
    // MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회원입니다."),
    //
    // 공구/상점(Store/GroupBuy): S001, G001 ...
    // OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "G001", "공구 준비 수량이 모두 소진되었습니다.");

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

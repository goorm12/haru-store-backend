package com.harustore.backend.common.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;

/**
 * 프론트엔드 입력값 검증(@Valid) 실패 시, 어떤 필드에서 왜 에러가 났는지 상세 정보를 전달하기 위한 DTO 클래스
 * JSON 변환 형태 예시:
 * {
 *   "field": "email",
 *   "rejectedValue": "invalid-email",
 *   "reason": "올바른 이메일 형식이 아닙니다."
 * }
 */
public class FieldErrorDetail {

    // 유효성 검사에 실패한 요청 필드명 (예: "email", "password", "page")
    private final String field;

    // 사용자가 실제로 입력했던 잘못된 값 (디버깅 및 사용자 안내용)
    private final Object rejectedValue;

    // 검증 실패 원인 메시지 (DTO 어노테이션의 message 속성값)
    private final String reason;

    public FieldErrorDetail(String field, Object rejectedValue, String reason) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.reason = reason;
    }

    /**
     * 스프링의 FieldError 객체를 우리 시스템의 FieldErrorDetail 객체로 변환하는 정적 팩토리 메서드
     */
    public static FieldErrorDetail of(FieldError fieldError) {
        return new FieldErrorDetail(
            fieldError.getField(),
            fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue(),
            fieldError.getDefaultMessage()
        );
    }

    /**
     * 스프링의 BindingResult(검증 결과 저장소)에 담긴 모든 필드 에러들을 FieldErrorDetail 리스트로 일괄 변환
     */
    public static List<FieldErrorDetail> of(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasFieldErrors()) {
            return Collections.emptyList();
        }

        return bindingResult.getFieldErrors()
            .stream()
            .map(FieldErrorDetail::of)
            .toList();
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public String getReason() {
        return reason;
    }
}

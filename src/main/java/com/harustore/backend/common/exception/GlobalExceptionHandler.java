package com.harustore.backend.common.exception;

import com.harustore.backend.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 애플리케이션 전역(@RestControllerAdvice)에서 발생하는 모든 예외를 중앙 집중식으로 가로채서
 * 일관된 HTTP 상태 코드와 ApiResponse 에러 JSON 본문으로 변환해주는 전역 예외 처리기(소방서)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서버 로그 기록을 위한 표준 SLF4J 로거
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 1. 비즈니스 로직 예외 처리
     * 서비스 계층 등에서 규칙 위반 시 throw new BusinessException(...)으로 던져진 예외를 처리합니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("비즈니스 예외 발생 - 코드: {}, 메시지: {}", errorCode.getCode(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.error(errorCode.getCode(), e.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 2. 클라이언트 입력값 유효성 검사(@Valid) 실패 예외 처리
     * DTO의 @NotBlank, @Email 등의 제약조건을 위반했을 때 스프링이 던지는 예외를 가로챕니다.
     * data 필드에 상세한 위반 필드 목록(FieldErrorDetail)을 함께 담아 반환합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDetail>>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("입력값 검증 실패(@Valid) - 대상: {}", e.getTarget() != null ? e.getTarget().getClass().getSimpleName() : "unknown");

        List<FieldErrorDetail> fieldErrors = FieldErrorDetail.of(e.getBindingResult());
        ApiResponse<List<FieldErrorDetail>> response = ApiResponse.error(
            ErrorCode.INVALID_INPUT_VALUE.getCode(),
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 3. 지원하지 않는 HTTP 메서드 호출 예외 처리
     * 예: GET 요청만 받는 엔드포인트에 POST로 요청한 경우
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("지원하지 않는 HTTP 메서드 호출: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.METHOD_NOT_ALLOWED.getCode(),
            ErrorCode.METHOD_NOT_ALLOWED.getMessage()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 4. 서버 내부 미처리 예외(NullPointerException 등 500 에러) 최종 방어
     * 우리가 미처 예측하지 못하고 발생한 시스템 에러를 잡아 사용자에게는 안전한 안내를 주고
     * 서버 콘솔에는 상세한 에러 로그(스택 트레이스)를 남깁니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("서버 내부 치명적 오류 발생: ", e);

        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

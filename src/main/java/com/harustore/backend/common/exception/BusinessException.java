package com.harustore.backend.common.exception;

/**
 * 프로젝트 전역에서 비즈니스 로직(Service 계층 등) 수행 중 규칙 위반이나 조건 미충족 시 발생하는 최상위 공통 예외 클래스
 * [사용 예시]
 * throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
 * throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호가 일치하지 않습니다.");
 */
public class BusinessException extends RuntimeException {

    // 예외의 원인과 대응되는 에러 명단표(ErrorCode) 정보
    private final ErrorCode errorCode;

    /**
     * ErrorCode에 정의된 기본 메시지를 사용하여 예외를 생성합니다.
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 상황에 따라 기본 메시지 대신 상세한 커스텀 메시지를 전달하고 싶을 때 사용합니다.
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드를 반환합니다. GlobalExceptionHandler에서 응답 생성 시 활용됩니다.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

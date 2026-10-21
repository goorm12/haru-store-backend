package com.harustore.backend.common.response;

// null 값인 필드를 JSON 변환(직렬화) 시 제외하기 위한 Jackson 어노테이션 임포트
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 프론트엔드와 백엔드 간의 모든 HTTP API 응답 규격을 일관되게 맞춰주는 공통 응답 래퍼 클래스
 * [응답 설계 규칙]
 * 1. 성공 시 (데이터 포함): { "success": true, "message": "...", "data": { ... } }
 * 2. 성공 시 (데이터 없음): { "success": true, "message": "...", "data": null }
 * 3. 실패 시 (에러 발생): { "success": false, "code": "ERROR_CODE", "message": "...", "data": null }
 * @param <T> 응답 본문에 담길 데이터의 제네릭 타입
 */
public class ApiResponse<T> {

    // API 호출 성공 여부 (성공: true, 실패: false)
    private final boolean success;

    // 클라이언트나 사용자에게 안내할 응답 메시지 (예: "성공", "상품이 성공적으로 등록되었습니다.", "남은 수량이 부족합니다.")
    private final String message;

    // 실제 전달할 비즈니스 데이터 본문 (데이터가 없는 경우에도 일관성을 위해 "data": null 형태로 노출됨)
    private final T data;

    // 실패(에러) 상황 시 프론트엔드가 에러 유형을 식별하고 예외 처리할 수 있도록 돕는 에러 코드
    // 성공 시에는 null이 되며, @JsonInclude(NON_NULL) 설정에 의해 JSON 결과에서 깔끔하게 자동 생략됩니다.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String code;

    /**
     * 외부에서 new 키워드로 무분별하게 객체를 직접 생성하지 못하도록 private 생성자로 제한합니다.
     * 객체 생성은 아래 정의된 명확한 의미의 정적 팩토리 메서드(success, error)를 통해서만 진행합니다.
     */
    private ApiResponse(boolean success, String message, T data, String code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    /**
     * 성공 응답 팩토리 메서드 (데이터만 전달하는 경우, 기본 메시지는 "성공"으로 설정)
     * 예: return ApiResponse.success(userInfo);
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data, null);
    }

    /**
     * 성공 응답 팩토리 메서드 (커스텀 메시지와 함께 데이터를 전달하는 경우)
     * 예: return ApiResponse.success("회원가입이 완료되었습니다.", userInfo);
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * 성공 응답 팩토리 메서드 (전달할 데이터가 없는 단순 작업 완료의 경우 - 예: 회원탈퇴, 로그아웃, 삭제 등)
     * "data": null 형태로 안전하게 직렬화됩니다.
     * 예: return ApiResponse.success("성공적으로 삭제되었습니다.");
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    /**
     * 실패(에러) 응답 팩토리 메서드 (예외 처리 및 GlobalExceptionHandler에서 사용)
     * 실패 시에는 에러 식별 코드(code)와 원인 메시지(message)를 함께 내려주며, data는 null로 설정됩니다.
     * 예: return ApiResponse.error("OUT_OF_STOCK", "남은 공구 수량이 부족합니다.");
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, message, null, code);
    }

    /**
     * 성공 여부를 반환 (Jackson 직렬화 시 isSuccess -> "success" JSON 필드 키로 매핑됨)
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 안내 메시지를 반환 ("message" JSON 필드 키로 매핑됨)
     */
    public String getMessage() {
        return message;
    }

    /**
     * 응답 데이터 본문을 반환 ("data" JSON 필드 키로 매핑됨)
     */
    public T getData() {
        return data;
    }

    /**
     * 에러 코드를 반환 ("code" JSON 필드 키로 매핑됨, null인 경우 JSON에서 생략됨)
     */
    public String getCode() {
        return code;
    }
}

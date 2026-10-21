package com.harustore.backend.common.response;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("성공 응답 - 데이터만 전달하는 경우 기본 메시지와 함께 정상 직렬화된다 (code 필드 제외)")
    void successWithDataOnly() throws Exception {
        // given
        Map<String, String> data = Map.of("title", "스프링 부트 완벽 가이드");

        // when
        ApiResponse<Map<String, String>> response = ApiResponse.success(data);
        String json = objectMapper.writeValueAsString(response);
        JsonNode jsonNode = objectMapper.readTree(json);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("성공");
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getCode()).isNull();

        // JSON 검증: code는 직렬화에서 제외되고 data, success, message는 유지됨
        assertThat(jsonNode.get("success").asBoolean()).isTrue();
        assertThat(jsonNode.get("message").asText()).isEqualTo("성공");
        assertThat(jsonNode.get("data").get("title").asText()).isEqualTo("스프링 부트 완벽 가이드");
        assertThat(jsonNode.has("code")).isFalse();
    }

    @Test
    @DisplayName("성공 응답 - 커스텀 메시지와 데이터를 전달하는 경우 정상 직렬화된다")
    void successWithMessageAndData() throws Exception {
        // given
        String message = "회원가입이 완료되었습니다.";
        Long userId = 100L;

        // when
        ApiResponse<Long> response = ApiResponse.success(message, userId);
        String json = objectMapper.writeValueAsString(response);
        JsonNode jsonNode = objectMapper.readTree(json);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isEqualTo(userId);
        assertThat(response.getCode()).isNull();

        // JSON 검증
        assertThat(jsonNode.get("success").asBoolean()).isTrue();
        assertThat(jsonNode.get("message").asText()).isEqualTo(message);
        assertThat(jsonNode.get("data").asLong()).isEqualTo(100L);
        assertThat(jsonNode.has("code")).isFalse();
    }

    @Test
    @DisplayName("성공 응답 - 데이터가 없는 경우 data 키는 null로 유지되고 code 필드는 제외된다")
    void successWithoutData() throws Exception {
        // given
        String message = "삭제가 정상적으로 완료되었습니다.";

        // when
        ApiResponse<Void> response = ApiResponse.success(message);
        String json = objectMapper.writeValueAsString(response);
        JsonNode jsonNode = objectMapper.readTree(json);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isNull();
        assertThat(response.getCode()).isNull();

        // JSON 검증: data 키가 JSON에 그대로 존재하며 null 값인지 확인
        assertThat(jsonNode.get("success").asBoolean()).isTrue();
        assertThat(jsonNode.get("message").asText()).isEqualTo(message);
        assertThat(jsonNode.has("data")).isTrue();
        assertThat(jsonNode.get("data").isNull()).isTrue();
        assertThat(jsonNode.has("code")).isFalse();
    }

    @Test
    @DisplayName("에러 응답 - code와 message가 포함되고 data 키는 null로 직렬화된다")
    void errorResponse() throws Exception {
        // given
        String errorCode = "OUT_OF_STOCK";
        String errorMessage = "남은 공구 수량이 부족합니다.";

        // when
        ApiResponse<Void> response = ApiResponse.error(errorCode, errorMessage);
        String json = objectMapper.writeValueAsString(response);
        JsonNode jsonNode = objectMapper.readTree(json);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(errorCode);
        assertThat(response.getMessage()).isEqualTo(errorMessage);
        assertThat(response.getData()).isNull();

        // JSON 검증: success: false, code, message, data: null 모두 포함
        assertThat(jsonNode.get("success").asBoolean()).isFalse();
        assertThat(jsonNode.get("code").asText()).isEqualTo(errorCode);
        assertThat(jsonNode.get("message").asText()).isEqualTo(errorMessage);
        assertThat(jsonNode.has("data")).isTrue();
        assertThat(jsonNode.get("data").isNull()).isTrue();
    }
}

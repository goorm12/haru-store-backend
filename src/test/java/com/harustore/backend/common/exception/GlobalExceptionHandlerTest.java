package com.harustore.backend.common.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 스프링 전체를 띄우지 않고, 테스트용 가짜 컨트롤러와 우리가 만든 GlobalExceptionHandler만 연결하여 초고속 단위 테스트 수행
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("비즈니스 예외(BusinessException) 발생 시 매핑된 HTTP 상태 코드와 ApiResponse 에러 JSON을 반환한다")
    void handleBusinessException() throws Exception {
        mockMvc.perform(get("/test/business-exception"))
            .andExpect(status().isNotFound()) // 404 상태 코드 확인
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C003"))
            .andExpect(jsonPath("$.message").value("요청한 대상을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("비즈니스 예외에 커스텀 메시지를 전달한 경우 커스텀 메시지가 정상 반영된다")
    void handleBusinessExceptionWithCustomMessage() throws Exception {
        mockMvc.perform(get("/test/business-custom-message"))
            .andExpect(status().isBadRequest()) // 400 상태 코드 확인
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C001"))
            .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("@Valid 유효성 검사 실패 시 400 상태 코드와 함께 data에 위반 필드 목록을 반환한다")
    void handleValidationException() throws Exception {
        // given: 필수값과 이메일 형식을 위반한 JSON 전송
        String invalidJson = """
            {
              "email": "not-an-email",
              "nickname": ""
            }
            """;

        // when & then
        mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest()) // 400 상태 코드 확인
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C001"))
            .andExpect(jsonPath("$.message").value("잘못된 입력값입니다."))
            .andExpect(jsonPath("$.data").isArray()) // data 필드에 배열 형태로 필드 에러 목록 포함
            .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드로 요청 시 405 상태 코드와 METHOD_NOT_ALLOWED 에러를 반환한다")
    void handleMethodNotSupported() throws Exception {
        // given: GET만 지원하는 엔드포인트에 POST 요청 전송
        mockMvc.perform(post("/test/business-exception"))
            .andExpect(status().isMethodNotAllowed()) // 405 상태 코드 확인
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C002"))
            .andExpect(jsonPath("$.message").value("지원하지 않는 HTTP 메서드입니다."));
    }

    @Test
    @DisplayName("예측하지 못한 런타임 에러(500) 발생 시 500 상태 코드와 안전한 시스템 에러 메시지를 반환한다")
    void handleUnhandledException() throws Exception {
        mockMvc.perform(get("/test/unhandled-exception"))
            .andExpect(status().isInternalServerError()) // 500 상태 코드 확인
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C004"))
            .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."));
    }

    // ==========================================
    // 예외 발생 시뮬레이션을 위한 테스트 전용 가짜 컨트롤러 & DTO
    // ==========================================
    @RestController
    static class TestController {

        @GetMapping("/test/business-exception")
        public void throwBusinessException() {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
        }

        @GetMapping("/test/business-custom-message")
        public void throwBusinessCustomMessage() {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호가 일치하지 않습니다.");
        }

        @PostMapping("/test/validation")
        public void testValidation(@Valid @RequestBody TestRequest request) {
            // 성공 로직
        }

        @GetMapping("/test/unhandled-exception")
        public void throwUnhandledException() {
            throw new NullPointerException("시뮬레이션: 예상치 못한 널 포인터 에러");
        }
    }

    static class TestRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }
}

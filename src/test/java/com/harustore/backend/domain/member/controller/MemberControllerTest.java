package com.harustore.backend.domain.member.controller;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.common.exception.GlobalExceptionHandler;
import com.harustore.backend.domain.member.dto.MemberResponse;
import com.harustore.backend.domain.member.dto.MemberSignupRequest;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MemberController의 HTTP 요청/응답 및 @Valid 유효성 검증을 테스트하는 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService; // 가짜 서비스 주입

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        // 우리가 만든 GlobalExceptionHandler 소방서를 장착하여 실제 에러 응답 규격까지 완벽 검증
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("회원가입 API 성공 - 올바른 정보로 요청 시 200 OK와 함께 생성된 회원 ID를 반환한다")
    void signup_success() throws Exception {
        // given [준비]
        String validJson = """
            {
              "kakaoId": "kakao_12345",
              "name": "홍길동",
              "phoneNumber": "010-1234-5678"
            }
            """;

        given(memberService.signup(any(MemberSignupRequest.class))).willReturn(1L);

        // when & then [요청 및 검증]
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
            .andExpect(jsonPath("$.data").value(1L));
    }

    @Test
    @DisplayName("회원가입 API 실패 - 전화번호 형식이 올바르지 않으면 @Valid가 잡아서 400 에러를 반환한다")
    void signup_invalidPhoneNumber_failsValidation() throws Exception {
        // given: 전화번호 형식이 틀린 JSON
        String invalidPhoneJson = """
            {
              "kakaoId": "kakao_12345",
              "name": "홍길동",
              "phoneNumber": "010-123"
            }
            """;

        // when & then: 400 Bad Request와 FieldErrorDetail 응답 검증
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPhoneJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C001"))
            .andExpect(jsonPath("$.data[0].field").value("phoneNumber"))
            .andExpect(jsonPath("$.data[0].reason").value("올바른 휴대폰 번호 형식(010-XXXX-XXXX)이어야 합니다."));
    }

    @Test
    @DisplayName("회원가입 API 실패 - 이름이 빈칸이면 @Valid가 잡아서 400 에러를 반환한다")
    void signup_blankName_failsValidation() throws Exception {
        // given: 이름이 빈칸인 JSON
        String blankNameJson = """
            {
              "kakaoId": "kakao_12345",
              "name": "",
              "phoneNumber": "010-1234-5678"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(blankNameJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C001"))
            .andExpect(jsonPath("$.data[0].field").value("name"))
            .andExpect(jsonPath("$.data[0].reason").value("이름은 필수입니다."));
    }

    @Test
    @DisplayName("프로필 조회 API 성공 - 존재하는 회원 ID 조회 시 200 OK와 함께 회원 정보를 반환한다")
    void getProfile_success() throws Exception {
        // given [준비]
        Member member = new Member("kakao_12345", "홍길동", "010-1234-5678");
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberResponse response = new MemberResponse(member);

        given(memberService.getProfile(1L)).willReturn(response);

        // when & then [요청 및 검증]
        mockMvc.perform(get("/api/members/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.kakaoId").value("kakao_12345"))
            .andExpect(jsonPath("$.data.name").value("홍길동"))
            .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
            .andExpect(jsonPath("$.data.role").value("BUYER"))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("프로필 조회 API 실패 - 존재하지 않는 회원 ID 조회 시 404 Not Found 에러를 반환한다")
    void getProfile_notFound() throws Exception {
        // given
        given(memberService.getProfile(999L))
            .willThrow(new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 회원입니다."));

        // when & then
        mockMvc.perform(get("/api/members/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("C003"))
            .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
    }
}

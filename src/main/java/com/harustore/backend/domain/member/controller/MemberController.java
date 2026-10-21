package com.harustore.backend.domain.member.controller;



import com.harustore.backend.common.response.ApiResponse;
import com.harustore.backend.domain.member.dto.MemberResponse;
import com.harustore.backend.domain.member.dto.MemberSignupRequest;
import com.harustore.backend.domain.member.repository.MemberRepository;
import com.harustore.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController // 1. REST API 전용 컨트롤러 선언 (JSON 자동 응답)
@RequestMapping("/api/members") // 2. 회원 관련 기본 URL 경로
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /*
     * 1. 회원가입 API (POST /api/members)
     * @Valid를 붙여서 빈칸이나 전화번호 형식이 틀리면 소방서(GlobalExceptionHandler)로 바로 쫓아냅니다!
     */
    @PostMapping
    public ApiResponse<Long> signup(@Valid @RequestBody MemberSignupRequest request) {
        Long memberId = memberService.signup(request);
        return ApiResponse.success("회원가입이 완료되었습니다.", memberId);
    }

    /*
     * 2. 회원 프로필 단건 조회 API (GET /api/members/{id})
     * TODO: 추후 Phase 6에서 카카오 OAuth2 & JWT 인증 연동 시,
     * 로그인한 본인 전용 마이페이지 조회를 위한 GET /api/members/me 추가 예정
     */
    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> getProfile(@PathVariable Long id) {
        MemberResponse response = memberService.getProfile(id);
        return ApiResponse.success(response);
    }

}

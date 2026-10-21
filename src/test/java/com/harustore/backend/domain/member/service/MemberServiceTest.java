package com.harustore.backend.domain.member.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.member.dto.MemberResponse;
import com.harustore.backend.domain.member.dto.MemberSignupRequest;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.entity.MemberRole;
import com.harustore.backend.domain.member.entity.MemberStatus;
import com.harustore.backend.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * MemberService의 순수 비즈니스 로직을 검증하는 단위 테스트 (Mockito 활용)
 * 무거운 스프링 전체를 띄우지 않고 0.1초 만에 로직의 정합성을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository; // 가짜(Mock) 리포지토리 주입

    @InjectMocks
    private MemberService memberService; // 가짜 리포지토리를 주입받은 테스트 대상 서비스

    @Test
    @DisplayName("회원가입 성공 - 중복되지 않은 카카오 ID로 가입 시 회원이 정상 저장되고 ID가 반환된다")
    void signup_success() {
        // given [준비]
        MemberSignupRequest request = new MemberSignupRequest("kakao_123", "홍길동", "010-1234-5678");
        Member savedMember = new Member("kakao_123", "홍길동", "010-1234-5678");
        ReflectionTestUtils.setField(savedMember, "id", 1L); // DB가 채워줄 가짜 ID 주입

        // 가짜 리포지토리 행동 정의: 중복 없음(false), 저장 시 savedMember 반환
        given(memberRepository.existsByKakaoId("kakao_123")).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when [실행]
        Long memberId = memberService.signup(request);

        // then [검증]
        assertThat(memberId).isEqualTo(1L);
        verify(memberRepository).save(any(Member.class)); // save 함수가 진짜 불렸는지 검증
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 가입된 카카오 ID인 경우 BusinessException 예외가 발생한다")
    void signup_duplicateKakaoId_throwsException() {
        // given [준비]
        MemberSignupRequest request = new MemberSignupRequest("kakao_123", "홍길동", "010-1234-5678");

        // 가짜 리포지토리: 이미 존재하는 카카오 ID라고 응답(true)
        given(memberRepository.existsByKakaoId("kakao_123")).willReturn(true);

        // when & then [실행 및 예외 검증]
        assertThatThrownBy(() -> memberService.signup(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    @DisplayName("프로필 조회 성공 - 존재하는 회원 ID로 조회 시 회원 상세 정보 DTO가 반환된다")
    void getProfile_success() {
        // given [준비]
        Long memberId = 1L;
        Member member = new Member("kakao_123", "홍길동", "010-1234-5678");
        ReflectionTestUtils.setField(member, "id", memberId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when [실행]
        MemberResponse response = memberService.getProfile(memberId);

        // then [검증]
        assertThat(response.getId()).isEqualTo(memberId);
        assertThat(response.getKakaoId()).isEqualTo("kakao_123");
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getRole()).isEqualTo(MemberRole.BUYER);
        assertThat(response.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("프로필 조회 실패 - 존재하지 않는 회원 ID로 조회 시 ENTITY_NOT_FOUND 예외가 발생한다")
    void getProfile_notFound_throwsException() {
        // given [준비]
        Long nonExistingId = 999L;
        given(memberRepository.findById(nonExistingId)).willReturn(Optional.empty()); // DB에 없음

        // when & then [실행 및 예외 검증]
        assertThatThrownBy(() -> memberService.getProfile(nonExistingId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENTITY_NOT_FOUND);
    }
}

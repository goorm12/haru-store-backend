package com.harustore.backend.domain.member.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.member.dto.MemberResponse;
import com.harustore.backend.domain.member.dto.MemberSignupRequest;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.repository.MemberRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 1. 스프링 서비스 빈으로 등록
@Transactional(readOnly = true) // 2. 기본적으로 모든 읽기 작업의 성능을 최적화!
public class MemberService {
    private final MemberRepository memberRepository;


    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    /*
      1. 회원가입 로직
      DB에 데이터를 새로 쓰는 작업이므로 @Transactional을 붙여 쓰기 모드로 전환합니다.
     */
    @Transactional
    public Long signup(MemberSignupRequest request) {
        // [규칙 1] 이미 가입된 카카오 ID인지 검사 (만들어놓은 existsByKakaoId 사용)
        if(memberRepository.existsByKakaoId((request.getKakaoId()))) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 가입된 카카오 계정입니다.");
        }

        // [규칙 2] 통과했으면 새로운 Member 엔티티 조립
        Member member = new Member(request.getKakaoId(), request.getName(), request.getPhoneNumber());

        // [규칙 3] DB에 저장
        Member savedMember = memberRepository.save(member);

        // 생성된 회원의 ID 번호 반환
        return savedMember.getId();
    }


    /*
      2. 회원 프로필 단건 조회 로직
     */
    public MemberResponse getProfile(Long id) {
        // DB에서 회원을 찾고, 없으면 ENTITY_NOT_FOUND에러를 던집니다.
        Member member = memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,"존재하지 않는 회원입니다."));

        // 찾은 회원 엔티티를 응답 DTO(MemberResponse)에 쏙 담아서 반환
        return new  MemberResponse(member);
    }

}

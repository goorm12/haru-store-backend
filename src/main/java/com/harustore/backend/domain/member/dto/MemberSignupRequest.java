package com.harustore.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberSignupRequest {

    @NotBlank(message="카카오 ID는 필수입니다.")
    private String kakaoId;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "연락처는 필수입니다.")
    @Pattern( regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$",
            message = "올바른 휴대폰 번호 형식(010-XXXX-XXXX)이어야 합니다." )
    private String phoneNumber;

    // 기본 생성자 (스프링이 JSON을 자바 객체로 바꿀 때 필요)
    protected MemberSignupRequest() {
    }
    public MemberSignupRequest(String kakaoId, String name, String phoneNumber) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    public String getKakaoId() { return kakaoId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
}

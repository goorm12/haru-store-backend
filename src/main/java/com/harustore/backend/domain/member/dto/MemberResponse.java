package com.harustore.backend.domain.member.dto;

import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.entity.MemberRole;
import com.harustore.backend.domain.member.entity.MemberStatus;

import java.time.LocalDateTime;

public class MemberResponse {
    private final Long id;
    private final String kakaoId;
    private final String name;
    private final String phoneNumber;
    private final MemberRole role;
    private final MemberStatus status;
    private final LocalDateTime createdAt;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.kakaoId = member.getKakaoId();
        this.name = member.getName();
        this.phoneNumber = member.getPhoneNumber();
        this.role = member.getRole();
        this.status = member.getStatus();
        this.createdAt = member.getCreatedAt();
    }


    public Long getId() { return id; }
    public String getKakaoId() { return kakaoId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public MemberRole getRole() { return role; }
    public MemberStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

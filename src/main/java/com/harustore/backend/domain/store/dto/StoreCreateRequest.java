package com.harustore.backend.domain.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class StoreCreateRequest {

    @NotNull(message="회원 ID는 필수입니다.")
    private Long memberId;

    @NotBlank(message = "상점명은 필수입니다.")
    private String name;

    private String description;

    @NotBlank(message="상점 주소는 필수입니다.")
    private String address;

    @NotBlank(message="상점 연락처는 필수입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
             message = "올바른 전화번호 형식(예: 02-123-4567 또는 010-1234-5678)이어야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "상점 운영시간은 필수입니다.")
    private String operatingHours;

    protected StoreCreateRequest() {}

    public StoreCreateRequest(Long memberId, String name, String description, String address, String phoneNumber, String operatingHours) {
        this.memberId = memberId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
    }

    public Long getMemberId() {return memberId;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getAddress() {return address;}
    public String getPhoneNumber() {return phoneNumber;}
    public String getOperatingHours() {return operatingHours;}
}

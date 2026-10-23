package com.harustore.backend.domain.store.dto;

import jakarta.validation.constraints.Pattern;

public class StoreUpdateRequest {
    private String name;
    private String description;
    private String address;

    @Pattern(
            regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
            message = "올바른 전화번호 형식(예: 02-123-4567 또는 010-1234-5678)이어야 합니다."
    )
    private String phoneNumber;
    private String operatingHours;

    protected  StoreUpdateRequest() {}
    public StoreUpdateRequest(String name, String description, String address, String phoneNumber, String operatingHours) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
    }


    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getAddress() {return address;}
    public String getPhoneNumber() {return phoneNumber;}
    public String getOperatingHours() {return operatingHours;}


}

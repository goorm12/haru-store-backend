package com.harustore.backend.domain.store.dto;

import com.harustore.backend.domain.store.entity.Store;
import com.harustore.backend.domain.store.entity.StoreStatus;

import java.time.LocalDateTime;

public class StoreResponse {
    private final Long id;
    private final Long sellerId;
    private final String sellerName;
    private final String name;
    private final String description;
    private final String address;
    private final String phoneNumber;
    private final String operatingHours;
    private final StoreStatus status;
    private final LocalDateTime createdAt;


    public StoreResponse(Store store) {
        this.id = store.getId();
        this.sellerId = store.getSeller().getId();
        this.sellerName = store.getSeller().getName();
        this.name = store.getName();
        this.description = store.getDescription();
        this.address = store.getAddress();
        this.phoneNumber = store.getPhoneNumber();
        this.operatingHours = store.getOperatingHours();
        this.status = store.getStatus();
        this.createdAt = store.getCreatedAt();

    }

    public Long getId() { return id; }
    public Long getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getOperatingHours() { return operatingHours; }
    public StoreStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}

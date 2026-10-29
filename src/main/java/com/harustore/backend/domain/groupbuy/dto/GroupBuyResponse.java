package com.harustore.backend.domain.groupbuy.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.groupbuy.entity.GroupBuyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * [공구 판매글 상세페이지 응답 DTO]
 * 특정 공구의 모든 상세 정보(긴 본문 설명 description 포함)를 반환합니다.
 */
@JsonPropertyOrder({
        "id",
        "storeId",
        "storeName",
        "sellerId",
        "sellerName",
        "title",
        "description",
        "imageUrl",
        "unit",
        "price",
        "totalQuantity",
        "reservedQuantity",
        "remainingQuantity",
        "maxLimitPerUser",
        "pickupStartDate",
        "cancelRestricted",
        "status",
        "createdAt"
})
public class GroupBuyResponse {
    private final Long id;
    private final Long storeId;
    private final String storeName;
    private final Long sellerId;
    private final String sellerName;
    private final String title;
    private final String description;     // 👈 상세페이지용 긴 본문!
    private final String imageUrl;
    private final String unit;
    private final Long price;
    private final int totalQuantity;
    private final int reservedQuantity;
    private final int remainingQuantity;   // 👈 계산된 잔여 수량
    private final Integer maxLimitPerUser;
    private final LocalDate pickupStartDate;
    private final boolean cancelRestricted;
    private final GroupBuyStatus status;
    private final LocalDateTime createdAt;

    public GroupBuyResponse(GroupBuy groupBuy) {
        this.id = groupBuy.getId();
        this.storeId = groupBuy.getStore().getId();
        this.storeName = groupBuy.getStore().getName();
        this.sellerId = groupBuy.getStore().getSeller().getId();
        this.sellerName = groupBuy.getStore().getSeller().getName();
        this.title = groupBuy.getTitle();
        this.description = groupBuy.getDescription();
        this.imageUrl = groupBuy.getImageUrl();
        this.unit = groupBuy.getUnit();
        this.price = groupBuy.getPrice();
        this.totalQuantity = groupBuy.getTotalQuantity();
        this.reservedQuantity = groupBuy.getReservedQuantity();
        this.remainingQuantity = groupBuy.getRemainingQuantity();
        this.maxLimitPerUser = groupBuy.getMaxLimitPerUser();
        this.pickupStartDate = groupBuy.getPickupStartDate();
        this.cancelRestricted = groupBuy.isCancelRestricted();
        this.status = groupBuy.getStatus();
        this.createdAt = groupBuy.getCreatedAt();
    }

    public Long getId() { return id; }
    public Long getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    public Long getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getUnit() { return unit; }
    public Long getPrice() { return price; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public int getRemainingQuantity() { return remainingQuantity; }
    public Integer getMaxLimitPerUser() { return maxLimitPerUser; }
    public LocalDate getPickupStartDate() { return pickupStartDate; }
    public boolean isCancelRestricted() { return cancelRestricted; }
    public GroupBuyStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

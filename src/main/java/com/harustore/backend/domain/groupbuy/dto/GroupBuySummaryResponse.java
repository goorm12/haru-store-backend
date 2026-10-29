package com.harustore.backend.domain.groupbuy.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.groupbuy.entity.GroupBuyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [공구 목록 카드 뷰 응답 DTO]
 * 홈 화면이나 검색 둘러보기 목록에서 description을 제외한 가벼운 카드 정보를 반환합니다.
 */
@JsonPropertyOrder({
        "id",
        "storeId",
        "storeName",
        "title",
        "imageUrl",
        "unit",
        "price",
        "totalQuantity",
        "reservedQuantity",
        "remainingQuantity",
        "pickupStartDate",
        "status",
        "createdAt"
})
public class GroupBuySummaryResponse {

    private final Long id;
    private final Long storeId;
    private final String storeName;
    private final String title;
    private final String imageUrl;
    private final String unit;
    private final Long price;
    private final int totalQuantity;
    private final int reservedQuantity;
    private final int remainingQuantity;
    private final LocalDate pickupStartDate;
    private final GroupBuyStatus status;
    private final LocalDateTime createdAt;

    public GroupBuySummaryResponse(GroupBuy groupBuy) {
        this.id = groupBuy.getId();
        this.storeId = groupBuy.getStore().getId();
        this.storeName = groupBuy.getStore().getName();
        this.title = groupBuy.getTitle();
        this.imageUrl = groupBuy.getImageUrl();
        this.unit = groupBuy.getUnit();
        this.price = groupBuy.getPrice();
        this.totalQuantity = groupBuy.getTotalQuantity();
        this.reservedQuantity = groupBuy.getReservedQuantity();
        this.remainingQuantity = groupBuy.getRemainingQuantity();
        this.pickupStartDate = groupBuy.getPickupStartDate();
        this.status = groupBuy.getStatus();
        this.createdAt = groupBuy.getCreatedAt();
    }
    public Long getId() { return id; }
    public Long getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getUnit() { return unit; }
    public Long getPrice() { return price; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public int getRemainingQuantity() { return remainingQuantity; }
    public LocalDate getPickupStartDate() { return pickupStartDate; }
    public GroupBuyStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

package com.harustore.backend.domain.groupbuy.dto;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;

/**
 * [공구 판매글 부분 수정 요청 DTO (PATCH)]
 * 사장님이 변경하고자 하는 필드만 선택적으로 전달받습니다.
 * 전달되지 않은 필드는 null로 유지되며, null인 필드는 기존 값을 그대로 보존합니다.
 */
public class GroupBuyUpdateRequest {

    // 1. 변경할 제목 / 상품명 (null이면 기존 값 유지)
    private String title;

    // 2. 변경할 상세 설명 본문 (null이면 기존 값 유지)
    private String description;

    // 3. 변경할 대표 썸네일 이미지 URL (null이면 기존 값 유지)
    private String imageUrl;

    // 4. 변경할 판매 단위 (null이면 기존 값 유지)
    private String unit;

    // 5. 변경할 상품 단가 (null이면 기존 값 유지, 값 전달 시 0원 이상 필수)
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Long price;

    // 6. 변경할 총 모집 수량 (null이면 기존 값 유지, 값 전달 시 1개 이상 필수)
    // ⚠️ 비즈니스 규칙: 이미 예약된 수량(reservedQuantity)보다 작게 줄일 수는 없음 (서비스에서 검증)
    @Min(value = 1, message = "총 수량은 1개 이상이어야 합니다.")
    private Integer totalQuantity;

    // 7. 변경할 1인당 구매 제한 수량 (null이면 기존 값 유지, 값 전달 시 1개 이상)
    @Min(value = 1, message = "1인당 구매 한도는 1개 이상이어야 합니다.")
    private Integer maxLimitPerUser;

    // 8. 변경할 단일 픽업 시작일 (null이면 기존 값 유지)
    private LocalDate pickupStartDate;

    // 9. 변경할 직접 취소 제한 여부 (null이면 기존 값 유지)
    private Boolean cancelRestricted;

    protected GroupBuyUpdateRequest() {}

    public GroupBuyUpdateRequest(String title, String description, String imageUrl, String unit,
                                 Long price, Integer totalQuantity, Integer maxLimitPerUser,
                                 LocalDate pickupStartDate, Boolean cancelRestricted) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unit = unit;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.maxLimitPerUser = maxLimitPerUser;
        this.pickupStartDate = pickupStartDate;
        this.cancelRestricted = cancelRestricted; // null이면 null 그대로 유지하여 기존값 보존
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getUnit() { return unit; }
    public Long getPrice() { return price; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public Integer getMaxLimitPerUser() { return maxLimitPerUser; }
    public LocalDate getPickupStartDate() { return pickupStartDate; }
    public Boolean getCancelRestricted() { return cancelRestricted; }
}

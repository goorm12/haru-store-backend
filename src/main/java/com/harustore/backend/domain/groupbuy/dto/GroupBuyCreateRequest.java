package com.harustore.backend.domain.groupbuy.dto;

import com.harustore.backend.domain.groupbuy.entity.GroupBuyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * [공구 판매글 등록 요청 DTO]
 * 사장님이 새로운 공동구매를 개설할 때(임시저장 DRAFT 또는 즉시 공개 OPEN) 클라이언트로부터 전달받는 데이터입니다.
 */
public class GroupBuyCreateRequest {

    // 1. 공구를 개설할 상점 ID (어느 상점의 공구인지 식별)
    @NotNull(message = "상점 ID는 필수입니다.")
    private Long storeId;

    // 2. 공구 판매글 제목 / 상품명 (예: "충주 부사 햇꿀사과 1박스")
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    // 3. 상세 설명 (HTML 태그, 마크다운, 상세 사진 등이 포함될 수 있는 긴 본문 내용 - 선택)
    private String description;

    // 4. 대표 썸네일 이미지 URL (목록/카드에 노출될 사진 - 선택)
    private String imageUrl;

    // 5. 판매 단위 명칭 (예: "1박스(5kg)", "1팩", "1송이")
    @NotBlank(message = "판매 단위는 필수입니다.")
    private String unit;

    // 6. 상품 단가 (원화 기준 금액, 0원 이상)
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Long price;

    // 7. 총 모집 수량 (최소 1개 이상 필수)
    @NotNull(message = "총 모집 수량은 필수입니다.")
    @Min(value = 1, message = "총 수량은 1개 이상이어야 합니다.")
    private Integer totalQuantity;

    // 8. 1인당 구매 제한 수량 (선택 사항: null이면 무제한, 입력 시 1개 이상)
    @Min(value = 1, message = "1인당 구매 한도는 1개 이상이어야 합니다.")
    private Integer maxLimitPerUser;

    // 9. 단일 픽업 시작일 (하루상점 핵심 정책: 해당 날짜부터 상점에서 픽업 가능)
    @NotNull(message = "픽업 시작일은 필수입니다.")
    private LocalDate pickupStartDate;

    // 10. 직접 취소 제한 여부 (true인 경우 신선식품 등 구매자의 임의 직접 취소 불가)
    private Boolean cancelRestricted;

    // 11. 등록 상태 (DRAFT: 임시저장, OPEN: 즉시 공개 / null인 경우 기본 OPEN 처리)
    private GroupBuyStatus status;

    protected GroupBuyCreateRequest() {}

    public GroupBuyCreateRequest(Long storeId, String title, String description, String imageUrl,
                                 String unit, Long price, Integer totalQuantity, Integer maxLimitPerUser,
                                 LocalDate pickupStartDate, Boolean cancelRestricted, GroupBuyStatus status) {
        this.storeId = storeId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unit = unit;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.maxLimitPerUser = maxLimitPerUser;
        this.pickupStartDate = pickupStartDate;
        this.cancelRestricted = cancelRestricted != null ? cancelRestricted : false;
        this.status = status != null ? status : GroupBuyStatus.OPEN;
    }

    public Long getStoreId() { return storeId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getUnit() { return unit; }
    public Long getPrice() { return price; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public Integer getMaxLimitPerUser() { return maxLimitPerUser; }
    public LocalDate getPickupStartDate() { return pickupStartDate; }
    public Boolean getCancelRestricted() { return cancelRestricted; }
    public GroupBuyStatus getStatus() { return status; }
}

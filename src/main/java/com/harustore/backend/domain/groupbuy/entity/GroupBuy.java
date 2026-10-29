package com.harustore.backend.domain.groupbuy.entity;

import com.harustore.backend.common.entity.BaseEntity;
import com.harustore.backend.domain.store.entity.Store;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "group_buy")
public class GroupBuy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공구를 주최하는 상점 (다대일 지연 로딩)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // 공구 상품명 / 판매글 제목 (예: "충주 부사 햇꿀사과 1박스")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 상세페이지 본문 내용 (긴 텍스트, HTML, 본문 이미지 태그 등을 담기 위해 TEXT 타입 지정)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 목록/카드에 노출되는 대표 썸네일 이미지 URL
    @Column(name = "image_url")
    private String imageUrl;

    // 판매 단위 명칭 (예: "1박스(5kg)", "1팩", "1송이")
    @Column(name = "unit", nullable = false, length = 50)
    private String unit;

    // 상품 단가 (원화 기준 금액, 오버플로우 방지를 위해 Long 타입 적용)
    @Column(name = "price", nullable = false)
    private Long price;

    // 총 모집 수량 (예: 100개)
    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    // 현재까지 예약 확정된 수량 (기본값 0, 동시성/재고 관리의 핵심 기준)
    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    // 1인당 최대 구매 제한 수량 (null이면 구매 수량 제한 없음)
    @Column(name = "max_limit_per_user")
    private Integer maxLimitPerUser;

    // 단일 픽업 시작일 (하루상점 핵심 정책: 해당 일자부터 상점에서 현장 수령 가능)
    @Column(name = "pickup_start_date", nullable = false)
    private LocalDate pickupStartDate;

    // 직접 취소 제한 여부 (true인 경우 구매자의 임의 직접 취소가 제한되는 품목)
    @Column(name = "cancel_restricted", nullable = false)
    private boolean cancelRestricted;

    // 공구 진행 상태 (DRAFT: 작성중, OPEN: 접수중, CLOSED: 마감, CANCELLED: 전체취소)
    // 숫자가 아닌 문자열 그대로 DB에 저장되도록 STRING 타입 지정 (순서 변경에 따른 버그 방지)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GroupBuyStatus status;

    protected GroupBuy() {}

    public GroupBuy(Store store, String title, String description, String imageUrl, String unit,
                    Long price, int totalQuantity, Integer maxLimitPerUser,
                    LocalDate pickupStartDate, boolean cancelRestricted, GroupBuyStatus status) {
        this.store = store;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unit = unit;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.reservedQuantity = 0; // 신규 생성 시 예약 수량은 항상 0부터 시작
        this.maxLimitPerUser = maxLimitPerUser;
        this.pickupStartDate = pickupStartDate;
        this.cancelRestricted = cancelRestricted;
        this.status = status;
    }


    /**
     * 공구 상태 변경 (판매자 수동 마감 CLOSED, 재개 OPEN, 전체 취소 CANCELLED 등)
     */
    public void updateStatus(GroupBuyStatus status) {
        this.status = status;
    }

    /**
     * 공구 정보 수정
     */
    public void patchInfo(String title, String description, String imageUrl, String unit,
                          Long price, Integer totalQuantity, Integer maxLimitPerUser,
                          LocalDate pickupStartDate, Boolean cancelRestricted) {
        if (title != null && !title.isBlank()) this.title = title;
        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (unit != null && !unit.isBlank()) this.unit = unit;
        if (price != null) this.price = price;
        if (totalQuantity != null) this.totalQuantity = totalQuantity;
        if (maxLimitPerUser != null) this.maxLimitPerUser = maxLimitPerUser;
        if (pickupStartDate != null) this.pickupStartDate = pickupStartDate;
        if (cancelRestricted != null) this.cancelRestricted = cancelRestricted;
    }

    /**
     * 남은 잔여 수량(재고) 계산
     */
    public int getRemainingQuantity() {
        return this.totalQuantity - this.reservedQuantity;
    }

    // --- Getter 메서드 ---
    public Long getId() { return id; }
    public Store getStore() { return store; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getUnit() { return unit; }
    public Long getPrice() { return price; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public Integer getMaxLimitPerUser() { return maxLimitPerUser; }
    public LocalDate getPickupStartDate() { return pickupStartDate; }
    public boolean isCancelRestricted() { return cancelRestricted; }
    public GroupBuyStatus getStatus() { return status; }
}

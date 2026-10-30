package com.harustore.backend.domain.reservation.dto;

import com.harustore.backend.domain.reservation.entity.CancelBy;
import com.harustore.backend.domain.reservation.entity.Reservation;
import com.harustore.backend.domain.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [예약 상세 및 단건 응답 DTO]
 * - 예약 생성 완료 화면, 구매자 예약 상세 화면, 판매자 예약 관리 상세 화면에서 필요한 모든 정보를 제공합니다.
 * - 계약 당시의 스냅샷 정보(단가, 주소, 시간 등)를 그대로 전달합니다.
 */
public class ReservationResponse {

    // 1. 기본 식별자
    private Long id;
    private String reservationNumber;
    private Long buyerId;
    private Long groupBuyId;
    private String groupBuyTitle;

    // 2. 수량 및 금액 (스냅샷 기준)
    private int quantity;
    private Long unitPrice;
    private Long totalAmount;

    // 3. 픽업 및 상점 정보 (스냅샷 기준)
    private LocalDate pickupStartDate;
    private String pickupAddress;
    private String pickupHours;
    private boolean isCancellationRestricted;

    // 4. 예약자 정보 (스냅샷 기준)
    private String buyerName;
    private String buyerPhone;

    // 5. 진행 상태 및 이력
    private ReservationStatus status;
    private LocalDateTime pickedUpAt;
    private String cancelReason;
    private CancelBy cancelBy;
    private LocalDateTime canceledAt;
    private LocalDateTime createdAt;

    protected ReservationResponse() {}

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.reservationNumber = reservation.getReservationNumber();
        this.buyerId = reservation.getBuyer().getId();
        this.groupBuyId = reservation.getGroupBuy().getId();
        this.groupBuyTitle = reservation.getGroupBuy().getTitle();
        this.quantity = reservation.getQuantity();
        this.unitPrice = reservation.getUnitPrice();
        this.totalAmount = reservation.getTotalAmount();
        this.pickupStartDate = reservation.getPickupStartDate();
        this.pickupAddress = reservation.getPickupAddress();
        this.pickupHours = reservation.getPickupHours();
        this.isCancellationRestricted = reservation.isCancellationRestricted();
        this.buyerName = reservation.getBuyerName();
        this.buyerPhone = reservation.getBuyerPhone();
        this.status = reservation.getStatus();
        this.pickedUpAt = reservation.getPickedUpAt();
        this.cancelReason = reservation.getCancelReason();
        this.cancelBy = reservation.getCancelBy();
        this.canceledAt = reservation.getCanceledAt();
        this.createdAt = reservation.getCreatedAt();
    }

    // --- Getter 메서드 ---
    public Long getId() { return id; }
    public String getReservationNumber() { return reservationNumber; }
    public Long getBuyerId() { return buyerId; }
    public Long getGroupBuyId() { return groupBuyId; }
    public String getGroupBuyTitle() { return groupBuyTitle; }
    public int getQuantity() { return quantity; }
    public Long getUnitPrice() { return unitPrice; }
    public Long getTotalAmount() { return totalAmount; }
    public LocalDate getPickupStartDate() { return pickupStartDate; }
    public String getPickupAddress() { return pickupAddress; }
    public String getPickupHours() { return pickupHours; }
    public boolean isCancellationRestricted() { return isCancellationRestricted; }
    public String getBuyerName() { return buyerName; }
    public String getBuyerPhone() { return buyerPhone; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getPickedUpAt() { return pickedUpAt; }
    public String getCancelReason() { return cancelReason; }
    public CancelBy getCancelBy() { return cancelBy; }
    public LocalDateTime getCanceledAt() { return canceledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

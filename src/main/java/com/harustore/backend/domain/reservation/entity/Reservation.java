package com.harustore.backend.domain.reservation.entity;

import com.harustore.backend.common.entity.BaseEntity;
import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예약(Reservation) 엔티티
 * - 구매자의 공구 참여 정보 및 현장 수령 전후 상태를 관리
 * - 공구/상점 정보가 변경되더라도 계약 당시 조건을 보존하기 위해 상세 스냅샷 필드를 보유
 */
@Entity
@Table(name = "reservation")
public class Reservation extends BaseEntity {

    // ==========================================
    // 1. 기본 식별자 및 연관 관계
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 고유 예약 번호 (주문코드 식별자, 예: R261029-A1B2C3)
    @Column(name = "reservation_number", nullable = false, unique = true, length = 50)
    private String reservationNumber;

    // 예약한 구매자 회원 (다대일 지연 로딩)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Member buyer;

    // 예약 대상 공구 (다대일 지연 로딩)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_id", nullable = false)
    private GroupBuy groupBuy;

    // 예약 수량
    @Column(name = "quantity", nullable = false)
    private int quantity;


    // ==========================================
    // 2. 스냅샷 필드 (예약 시점의 정보를 불변으로 고정)
    // ==========================================

    // 예약 당시 개당 단가 (공구 가격이 사후에 인상/인하되어도 예약 당시 단가 고정보존)
    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    // 총 예상 결제 금액 (unitPrice * quantity)
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    // 단일 픽업 시작일 스냅샷 (해당 일자부터 현장 수령 가능)
    @Column(name = "pickup_start_date", nullable = false)
    private LocalDate pickupStartDate;

    // 픽업 매장 도로명 주소 스냅샷
    @Column(name = "pickup_address", nullable = false, length = 255)
    private String pickupAddress;

    // 픽업 매장 운영시간 안내 스냅샷 (예: "평일 10:00 ~ 20:00")
    @Column(name = "pickup_hours", nullable = false, length = 100)
    private String pickupHours;

    // 직접 취소 제한 품목 여부 스냅샷 (true인 경우 픽업 전날이어도 구매자 임의 취소 불가)
    @Column(name = "is_cancellation_restricted", nullable = false)
    private boolean isCancellationRestricted;

    // 예약 시점의 구매자 이름 스냅샷 (수령 시 현장 명단 대조용)
    @Column(name = "buyer_name", nullable = false, length = 50)
    private String buyerName;

    // 예약 시점의 구매자 연락처 스냅샷 (수령 시 현장 본인 대조 및 긴급 연락용)
    @Column(name = "buyer_phone", nullable = false, length = 20)
    private String buyerPhone;


    // ==========================================
    // 3. 상태 관리 및 처리 일시
    // ==========================================

    // 예약 진행 상태 (RESERVED, PICKED_UP, UNCLAIMED, CANCELED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;

    // 현장 수령(픽업) 완료 처리 일시 (SELL-04)
    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;


    // ==========================================
    // 4. 취소 및 정정 이력 추적
    // ==========================================

    // 취소 사유 (구매자 취소 사유 또는 판매자의 사유 입력)
    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    // 취소 주체 (BUYER, SELLER, ADMIN)
    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_by", length = 20)
    private CancelBy cancelBy;

    // 취소 완료 일시
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    // 상태 오기록 정정 사유 (SELL-06: 잘못된 픽업/미수령 상태 번복 및 정정 시 기록)
    @Column(name = "status_correction_reason", length = 255)
    private String statusCorrectionReason;


    // ==========================================
    // 5. 생성자 (Constructor)
    // ==========================================

    // JPA 스펙용 기본 생성자 (외부 임의 생성 방지를 위해 protected)
    protected Reservation() {}

    // 신규 예약 생성자 (초기 상태는 RESERVED로 확정)
    public Reservation(String reservationNumber, Member buyer, GroupBuy groupBuy, int quantity,
                       Long unitPrice, LocalDate pickupStartDate, String pickupAddress,
                       String pickupHours, boolean isCancellationRestricted,
                       String buyerName, String buyerPhone) {
        this.reservationNumber = reservationNumber;
        this.buyer = buyer;
        this.groupBuy = groupBuy;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice * quantity; // 총 금액 자동 계산
        this.pickupStartDate = pickupStartDate;
        this.pickupAddress = pickupAddress;
        this.pickupHours = pickupHours;
        this.isCancellationRestricted = isCancellationRestricted;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.status = ReservationStatus.RESERVED;
    }


    // ==========================================
    // 6. 비즈니스 도메인 메서드
    // ==========================================

    /**
     * [RSV-03] 예약 수량 변경
     * - 기존 예약 당시 단가(unitPrice)를 그대로 유지하여 총 금액(totalAmount) 재계산
     */
    public void changeQuantity(int newQuantity) {
        this.quantity = newQuantity;
        this.totalAmount = this.unitPrice * newQuantity;
    }

    /**
     * [RSV-05, SELL-02] 예약 취소 처리
     * - 취소 사유, 취소 주체, 취소 일시를 기록하고 상태를 CANCELED로 전환
     */
    public void cancel(String reason, CancelBy cancelBy) {
        this.status = ReservationStatus.CANCELED;
        this.cancelReason = reason;
        this.cancelBy = cancelBy;
        this.canceledAt = LocalDateTime.now();
    }

    /**
     * [SELL-04] 현장 수령(픽업) 완료 처리
     * - 판매자가 상품을 구매자에게 전달한 시점의 일시 기록 및 상태 변경
     */
    public void completePickup() {
        this.status = ReservationStatus.PICKED_UP;
        this.pickedUpAt = LocalDateTime.now();
    }

    /**
     * [SELL-05] 미수령 처리
     * - 픽업 시작일 경과 후 구매자가 방문하지 않았음을 판매자가 확인 처리
     */
    public void markUnclaimed() {
        this.status = ReservationStatus.UNCLAIMED;
    }

    /**
     * [SELL-06] 상태 오기록 정정
     * - 잘못 처리된 픽업 완료/미수령 상태를 사유와 함께 정정
     */
    public void correctStatus(ReservationStatus targetStatus, String reason) {
        this.status = targetStatus;
        this.statusCorrectionReason = reason;
        if (targetStatus == ReservationStatus.PICKED_UP && this.pickedUpAt == null) {
            this.pickedUpAt = LocalDateTime.now();
        }
    }


    // ==========================================
    // 7. Getter 메서드
    // ==========================================

    public Long getId() { return id; }
    public String getReservationNumber() { return reservationNumber; }
    public Member getBuyer() { return buyer; }
    public GroupBuy getGroupBuy() { return groupBuy; }
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
    public String getStatusCorrectionReason() { return statusCorrectionReason; }
}

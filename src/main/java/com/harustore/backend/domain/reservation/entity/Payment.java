package com.harustore.backend.domain.reservation.entity;

import com.harustore.backend.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 현장 결제 기록(Payment) 엔티티
 * - 예약 상태(수령 여부)와 분리되어 현장 결제 수단, 금액, 결제 일시 및 오기록 정정을 관리
 */
@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대상 예약 (일대일 지연 로딩, 고유 제약조건)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    // 현장 결제 수단 (CASH, CARD, TRANSFER, OTHER)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    // 실제 현장 결제된 금액
    @Column(name = "amount", nullable = false)
    private Long amount;

    // 결제 상태 (UNPAID: 미결제, PAID: 결제완료, CANCELED: 결제취소)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    // 현장 결제 완료 일시
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // 결제 오기록 정정 사유 (SELL-06: 결제 금액/수단 번복 및 정정 시 기록)
    @Column(name = "correction_reason", length = 255)
    private String correctionReason;

    // 결제 처리 또는 오기록을 정정한 판매자 회원 ID
    @Column(name = "processed_by_seller_id")
    private Long processedBySellerId;

    protected Payment() {}

    public Payment(Reservation reservation, PaymentMethod paymentMethod, Long amount,
                   PaymentStatus status, Long processedBySellerId) {
        this.reservation = reservation;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.processedBySellerId = processedBySellerId;
        if (status == PaymentStatus.PAID) {
            this.paidAt = LocalDateTime.now();
        }
    }

    /**
     * [SELL-03] 현장 결제 완료 처리
     */
    public void markPaid(PaymentMethod method, Long amount, Long sellerId) {
        this.paymentMethod = method;
        this.amount = amount;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.processedBySellerId = sellerId;
    }

    /**
     * [SELL-06] 결제 오기록 정정
     */
    public void correctPayment(PaymentMethod method, Long amount, PaymentStatus status,
                               String reason, Long sellerId) {
        this.paymentMethod = method;
        this.amount = amount;
        this.status = status;
        this.correctionReason = reason;
        this.processedBySellerId = sellerId;
    }

    // Getter 메서드
    public Long getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Long getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public String getCorrectionReason() { return correctionReason; }
    public Long getProcessedBySellerId() { return processedBySellerId; }
}

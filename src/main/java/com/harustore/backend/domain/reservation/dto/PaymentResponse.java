package com.harustore.backend.domain.reservation.dto;

import com.harustore.backend.domain.reservation.entity.Payment;
import com.harustore.backend.domain.reservation.entity.PaymentMethod;
import com.harustore.backend.domain.reservation.entity.PaymentStatus;

import java.time.LocalDateTime;

/**
 * [현장 결제 응답 DTO]
 * - 현장 결제 등록 및 정정 결과, 그리고 예약 상세 화면에서 결제 정보 확인 시 전달하는 데이터입니다.
 */
public class PaymentResponse {

    private Long id;
    private Long reservationId;
    private PaymentMethod paymentMethod;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private String correctionReason;
    private Long processedBySellerId;

    protected PaymentResponse() {}

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.reservationId = payment.getReservation().getId();
        this.paymentMethod = payment.getPaymentMethod();
        this.amount = payment.getAmount();
        this.status = payment.getStatus();
        this.paidAt = payment.getPaidAt();
        this.correctionReason = payment.getCorrectionReason();
        this.processedBySellerId = payment.getProcessedBySellerId();
    }

    public Long getId() { return id; }
    public Long getReservationId() { return reservationId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Long getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public String getCorrectionReason() { return correctionReason; }
    public Long getProcessedBySellerId() { return processedBySellerId; }
}

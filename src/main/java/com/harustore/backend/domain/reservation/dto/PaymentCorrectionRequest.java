package com.harustore.backend.domain.reservation.dto;

import com.harustore.backend.domain.reservation.entity.PaymentMethod;
import com.harustore.backend.domain.reservation.entity.PaymentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * [현장 결제 오기록 정정 요청 DTO]
 * - 판매자가 결제 수단, 금액, 결제 상태를 잘못 입력했을 때 사유와 함께 정정하는 데이터입니다.
 */
public class PaymentCorrectionRequest {

    // 정정할 결제 수단
    @NotNull(message = "결제 수단은 필수입니다.")
    private PaymentMethod paymentMethod;

    // 정정할 결제 금액
    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 0, message = "결제 금액은 0원 이상이어야 합니다.")
    private Long amount;

    // 정정할 결제 상태 (UNPAID: 미결제, PAID: 결제완료, CANCELED: 결제취소)
    @NotNull(message = "결제 상태는 필수입니다.")
    private PaymentStatus status;

    // 정정 사유 (필수, 최대 255자)
    @NotBlank(message = "정정 사유를 입력해 주세요.")
    @Size(max = 255, message = "정정 사유는 최대 255자까지 입력 가능합니다.")
    private String correctionReason;

    protected PaymentCorrectionRequest() {}

    public PaymentCorrectionRequest(PaymentMethod paymentMethod, Long amount, PaymentStatus status, String correctionReason) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.correctionReason = correctionReason;
    }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Long getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public String getCorrectionReason() { return correctionReason; }
}

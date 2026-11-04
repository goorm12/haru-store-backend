package com.harustore.backend.domain.reservation.dto;

import com.harustore.backend.domain.reservation.entity.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * [현장 결제 등록 요청 DTO]
 * - 판매자가 현장에서 구매자로부터 결제(현금, 카드, 계좌이체 등)를 확인하고 등록할 때 전달하는 데이터입니다.
 */
public class PaymentRecordRequest {

    // 현장 결제 수단 (CASH: 현금, CARD: 카드, TRANSFER: 계좌이체, OTHER: 기타)
    @NotNull(message = "결제 수단은 필수입니다.")
    private PaymentMethod paymentMethod;

    // 실제 현장 결제된 금액 (0원 이상)
    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 0, message = "결제 금액은 0원 이상이어야 합니다.")
    private Long amount;

    protected PaymentRecordRequest() {}

    public PaymentRecordRequest(PaymentMethod paymentMethod, Long amount) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Long getAmount() {
        return amount;
    }
}

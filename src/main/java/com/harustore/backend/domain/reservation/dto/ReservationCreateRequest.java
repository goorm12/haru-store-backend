package com.harustore.backend.domain.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * [예약 생성 요청 DTO]
 * - 구매자가 공구 상세 화면에서 수량을 선택하고 예약을 신청할 때 전달하는 데이터입니다.
 */
public class ReservationCreateRequest {

    // 예약 대상 공구 ID (필수)
    @NotNull(message = "공구 ID는 필수입니다.")
    private Long groupBuyId;

    // 예약 신청 수량 (최소 1개 이상 필수)
    @NotNull(message = "예약 수량은 필수입니다.")
    @Min(value = 1, message = "예약 수량은 최소 1개 이상이어야 합니다.")
    private Integer quantity;

    protected ReservationCreateRequest() {}

    public ReservationCreateRequest(Long groupBuyId, Integer quantity) {
        this.groupBuyId = groupBuyId;
        this.quantity = quantity;
    }

    public Long getGroupBuyId() {
        return groupBuyId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

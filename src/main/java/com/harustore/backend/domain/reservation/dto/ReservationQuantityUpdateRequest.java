package com.harustore.backend.domain.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * [예약 수량 변경 요청 DTO]
 * - 예약 완료 후 픽업 시작일 전날까지 구매자가 예약 수량을 늘리거나 줄일 때 전달하는 데이터입니다.
 */
public class ReservationQuantityUpdateRequest {

    // 새로 변경하고자 하는 목표 수량 (최소 1개 이상)
    @NotNull(message = "변경할 수량은 필수입니다.")
    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    private Integer newQuantity;

    protected ReservationQuantityUpdateRequest() {}

    public ReservationQuantityUpdateRequest(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }
}

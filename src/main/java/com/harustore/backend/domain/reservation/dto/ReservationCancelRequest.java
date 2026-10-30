package com.harustore.backend.domain.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * [예약 취소 요청 DTO]
 * - 구매자 직접 취소 또는 판매자가 관리 화면에서 개별 예약을 취소할 때 전달하는 데이터입니다.
 */
public class ReservationCancelRequest {

    // 취소 사유 (선택 또는 필수: 상세 사유 입력)
    @NotBlank(message = "취소 사유를 입력해 주세요.")
    @Size(max = 255, message = "취소 사유는 최대 255자까지 입력 가능합니다.")
    private String cancelReason;

    protected ReservationCancelRequest() {}

    public ReservationCancelRequest(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }
}

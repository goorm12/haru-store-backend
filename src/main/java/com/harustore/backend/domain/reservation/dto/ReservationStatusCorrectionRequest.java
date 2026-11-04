package com.harustore.backend.domain.reservation.dto;

import com.harustore.backend.domain.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * [예약 상태 오기록 정정 요청 DTO]
 * - 판매자가 픽업 완료나 미수령 처리를 실수로 잘못 눌렀을 때, 정정 대상 상태와 사유를 함께 전달하는 데이터입니다.
 */
public class ReservationStatusCorrectionRequest {

    // 정정하여 되돌리고자 하는 목표 상태 (예: RESERVED, PICKED_UP, UNCLAIMED)
    @NotNull(message = "정정할 예약 상태는 필수입니다.")
    private ReservationStatus targetStatus;

    // 상태 정정 사유 (필수, 최대 255자)
    @NotBlank(message = "정정 사유를 입력해 주세요.")
    @Size(max = 255, message = "정정 사유는 최대 255자까지 입력 가능합니다.")
    private String reason;

    protected ReservationStatusCorrectionRequest() {}

    public ReservationStatusCorrectionRequest(ReservationStatus targetStatus, String reason) {
        this.targetStatus = targetStatus;
        this.reason = reason;
    }

    public ReservationStatus getTargetStatus() { return targetStatus; }
    public String getReason() { return reason; }
}

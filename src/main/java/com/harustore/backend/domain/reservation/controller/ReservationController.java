package com.harustore.backend.domain.reservation.controller;

import com.harustore.backend.common.response.ApiResponse;
import com.harustore.backend.common.response.PageResponse;
import com.harustore.backend.domain.reservation.dto.ReservationCreateRequest;
import com.harustore.backend.domain.reservation.dto.ReservationResponse;
import com.harustore.backend.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * [구매자용 예약 컨트롤러 (Buyer)]
 * 기본 URL: /api/v1/reservations
 */
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // =========================================================================
    // API 1. [예약 생성 및 즉시 확정 (RSV-01)]
    // - HTTP 메서드 및 경로: POST /api/v1/reservations
    // - 설명: 구매자가 공구 상품과 수량을 선택해 예약을 신청하고 즉시 확정합니다.
    // - 추천 메서드명: createReservation
    // =========================================================================
    @PostMapping
    public ApiResponse<Long> createReservation(
            @RequestHeader("X-Member-Id") Long memberId,
            @Valid @RequestBody ReservationCreateRequest request) {
        Long reservationId = reservationService.createReservation(memberId, request);
        return ApiResponse.success("예약이 확정되었습니다.", reservationId);
    }


    // =========================================================================
    // API 2. [내 예약 목록 페이징 조회 (RSV-02)]
    // - HTTP 메서드 및 경로: GET /api/v1/reservations/me
    // - 설명: 로그인한 구매자가 본인의 예약 목록을 최신순으로 페이징 조회합니다.
    // - 추천 메서드명: getMyReservations
    // =========================================================================
    @GetMapping("/me")
    public ApiResponse<PageResponse<ReservationResponse>> getMyReservations(
            @RequestHeader("X-Member-Id") Long  memberId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC ,page=0, size=10) Pageable pageable
    ) {

        Page<ReservationResponse> reservation = reservationService.getMyReservations(memberId, pageable);

        return ApiResponse.success("내 예약 목록 조회가 완료되었습니다.", PageResponse.from(reservation));

    }


    // =========================================================================
    // API 3. [내 예약 단건 상세 조회 (RSV-02)]
    // - HTTP 메서드 및 경로: GET /api/v1/reservations/{reservationId}
    // - 설명: 본인의 특정 예약 1건의 영수증 상세 정보(단가, 주소, 시간 등)를 조회합니다.
    // - 추천 메서드명: getMyReservation
    // =========================================================================
    @GetMapping("/{reservationId}")
    public ApiResponse<ReservationResponse> getReservation(
            @RequestHeader("X-Member-Id") Long  memberId,
            @PathVariable Long reservationId
    ) {
        ReservationResponse response = reservationService.getMyReservation(memberId, reservationId);
        return ApiResponse.success("예약 상세 조회가 완료되었습니다.", response);
    }


    // =========================================================================
    // API 4. [예약 수량 변경 (RSV-03)]
    // - HTTP 메서드 및 경로: PATCH /api/v1/reservations/{reservationId}/quantity
    // - 설명: 픽업 시작일 전날까지 구매자가 예약 수량을 늘리거나 줄입니다.
    // - 추천 메서드명: updateQuantity
    // =========================================================================



    // =========================================================================
    // API 5. [구매자 직접 예약 취소 (RSV-05, RSV-06)]
    // - HTTP 메서드 및 경로: POST /api/v1/reservations/{reservationId}/cancel
    // - 설명: 픽업 시작일 전날까지 구매자가 본인의 예약을 직접 취소합니다.
    // - 추천 메서드명: cancelReservation
    // =========================================================================

}

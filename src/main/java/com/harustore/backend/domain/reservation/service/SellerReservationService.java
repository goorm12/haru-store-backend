package com.harustore.backend.domain.reservation.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.groupbuy.repository.GroupBuyRepository;
import com.harustore.backend.domain.member.repository.MemberRepository;
import com.harustore.backend.domain.reservation.dto.ReservationCancelRequest;
import com.harustore.backend.domain.reservation.dto.ReservationResponse;
import com.harustore.backend.domain.reservation.entity.CancelBy;
import com.harustore.backend.domain.reservation.entity.Reservation;
import com.harustore.backend.domain.reservation.entity.ReservationStatus;
import com.harustore.backend.domain.reservation.repository.PaymentRepository;
import com.harustore.backend.domain.reservation.repository.ReservationRepository;
import com.harustore.backend.domain.store.dto.StoreResponse;
import com.harustore.backend.domain.store.entity.Store;
import com.harustore.backend.domain.store.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [판매자용 예약 관리 및 현장 결제/수령 서비스 (Seller)]
 * - 상점별 예약 목록 조회 (SELL-01)
 * - 판매자 당일 예약 개별 사유 취소 (SELL-02)
 * - 현장 결제 등록 및 처리 (SELL-03)
 * - 현장 수령(픽업) 완료 처리 (SELL-04)
 * - 미수령(노쇼) 처리 (SELL-05)
 * - 현장 결제 및 수령 상태 오기록 정정 (SELL-06)
 */
@Service
@Transactional(readOnly = true)
public class SellerReservationService {

    private final ReservationRepository reservationRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;

    public SellerReservationService(ReservationRepository reservationRepository,
                                    GroupBuyRepository groupBuyRepository,
                                    MemberRepository memberRepository,
                                    StoreRepository storeRepository,
                                    PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.groupBuyRepository = groupBuyRepository;
        this.memberRepository = memberRepository;
        this.storeRepository = storeRepository;
        this.paymentRepository = paymentRepository;
    }

    // =========================================================================
    // 기능 1. [판매자 상점별 예약 목록 페이징 조회 (SELL-01)]
    // - 설명: 사장님이 본인 상점에 들어온 예약 내역을 최신순 또는 상태별로 페이징 조회합니다.
    // - 처리 순서:
    //   1단계: 상점 존재 여부 및 사장님 본인 소유권 검증 (해당 상점의 판매자가 맞는지 확인)
    //   2단계: 상태(status) 필터가 들어왔는지 확인
    //          - 특정 상태가 지정된 경우: 상태별 페이징 조회 메서드 호출
    //          - 상태가 null인 경우: 해당 상점의 전체 예약 최신순 페이징 조회 메서드 호출
    //   3단계: 조회된 예약 목록을 응답 데이터(ReservationResponse)로 변환하여 반환
    // =========================================================================
    public Page<ReservationResponse> getMyStoreReservations(
            Long sellerId,
            Long storeId,
            ReservationStatus status,
            Pageable pageable) {
        Page<Reservation> reservationPage;
        // [1단계] 상점 존재 여부 및 사장님 본인 소유권 검증 (해당 상점의 판매자가 맞는지 확인)
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        if (!store.getSeller().getId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [2단계] 상태(status) 필터가 들어왔는지 확인
        //  - 특정 상태가 지정된 경우: 상태별 페이징 조회 메서드 호출
        //  - 상태가 null인 경우: 해당 상점의 전체 예약 최신순 페이징 조회 메서드 호출
        if(status != null) {
            reservationPage = reservationRepository.findByGroupBuyStoreIdAndStatus(storeId, status, pageable);
        } else {
            reservationPage = reservationRepository.findByGroupBuyStoreId(storeId, pageable);
        }

        //  [3단계] : 조회된 예약 목록을 응답 데이터(ReservationResponse)로 변환하여 반환

        return reservationPage.map(ReservationResponse::new);
    }


    // =========================================================================
    // 기능 2. [판매자 예약 개별 사유 취소 (SELL-02)]
    // - 설명: 사장님이 특정 예약을 사유를 입력하고 직접 취소합니다. (당일에도 관리자 승인 없이 즉시 가능)
    // - 처리 순서:
    //   1단계: 대상 예약 정보를 자물쇠 걸고 조회
    //   2단계: 사장님 소유권 검증 (이 예약이 걸린 상점의 판매자가 요청한 사장님이 맞는지 확인)
    //   3단계: 현재 상태 확인 (오직 예약 완료인 RESERVED 상태만 취소 가능! 이미 취소, 픽업완료, 미수령은 취소 불가)
    //   4단계: 공구 장부를 자물쇠 걸고 조회하여 취소된 수량만큼 예약 수량 감소 (재고 반환)
    //   5단계: 예약 상태를 취소로 변경하고, 취소 사유, 취소 주체(SELLER), 취소 일시 기록
    //   6단계: 취소된 예약 결과 반환
    // =========================================================================
    @Transactional
    public ReservationResponse cancelReservationBySeller(
            Long sellerId,
            Long reservationId,
            ReservationCancelRequest request
    ) {
        //   [1단계] 대상 예약 정보를 자물쇠 걸고 조회
        Reservation reservation = reservationRepository.findByIdWithLock(reservationId)
                .orElseThrow(()-> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        //   [2단계] 사장님 소유권 검증 (이 예약이 걸린 상점의 판매자가 요청한 사장님이 맞는지 확인)
        if(!reservation.getGroupBuy().getStore().getSeller().getId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 상점 판매자만 취소할 수 있습니다.");
        }

        //  [3단계] 현재 상태 확인 (오직 예약 완료인 RESERVED 상태만 취소 가능! 이미 취소, 픽업완료, 미수령은 취소 불가)
        if(reservation.getStatus() !=  ReservationStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS,"예약 완료 상태의 예약만 취소할 수 있습니다.");
        }

        // [4단계] 공구 장부를 자물쇠 걸고 조회하여 취소된 수량만큼 예약 수량 감소 (재고 반환)
        GroupBuy groupBuy = groupBuyRepository.findByIdWithLock(reservation.getGroupBuy().getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));
        groupBuy.decreaseReservedQuantity(reservation.getQuantity());

        // [5단계] 예약 상태를 취소로 변경하고, 취소 사유, 취소 주체(SELLER), 취소 일시 기록
        reservation.cancel(request.getCancelReason(), CancelBy.SELLER);

        // [6단계] 취소된 예약 결과 반환
        return new ReservationResponse(reservation);
    }


    // =========================================================================
    // 기능 3. [현장 수령(픽업) 완료 처리 (SELL-04)]
    // - 설명: 사장님이 매장에서 손님에게 상품을 전달하고 '수령 완료' 버튼을 누릅니다.
    // - 처리 순서:
    //   1단계: 대상 예약 정보를 자물쇠 걸고 조회
    //   2단계: 사장님 소유권 검증 (해당 상점의 판매자가 맞는지 확인)
    //   3단계: 현재 상태 확인 (오직 RESERVED 상태에서만 픽업 완료로 전환 가능)
    //   4단계: 픽업 완료 처리 (상태를 PICKED_UP으로 변경하고 픽업 일시 기록)
    //          ※ ⚠️ 중요: 픽업 완료는 물건을 건네준 것이므로 재고(reservedQuantity)를 반환하지 않습니다!
    //   5단계: 변경된 예약 결과 반환
    // =========================================================================



    // =========================================================================
    // 기능 4. [미수령(노쇼) 처리 (SELL-05)]
    // - 설명: 픽업일이 지났는데도 손님이 방문하지 않았음을 사장님이 확인 처리합니다.
    // - 처리 순서:
    //   1단계: 대상 예약 정보를 자물쇠 걸고 조회
    //   2단계: 사장님 소유권 검증 (해당 상점의 판매자가 맞는지 확인)
    //   3단계: 현재 상태 확인 (오직 RESERVED 상태에서만 미수령으로 전환 가능)
    //   4단계: 미수령 처리 (상태를 UNCLAIMED로 변경)
    //          ※ ⚠️ 중요: 날짜 경과로 자동 취소/미수령하지 않으며, 미수령 처리 시에도 재고를 반환하지 않습니다!
    //   5단계: 변경된 예약 결과 반환
    // =========================================================================



    // =========================================================================
    // 기능 5. [현장 결제 등록 및 처리 (SELL-03)]
    // - 설명: 손님이 매장에서 현금, 카드, 계좌이체 등으로 결제한 내역을 사장님이 수기 등록합니다.
    // - 처리 순서:
    //   1단계: 대상 예약 정보 조회
    //   2단계: 사장님 소유권 검증 (해당 상점의 판매자가 맞는지 확인)
    //   3단계: 해당 예약에 이미 등록된 결제 기록이 있는지 확인 (있다면 기존 기록에 markPaid 호출, 없다면 신규 Payment 생성 후 저장)
    //   4단계: 결제 수단, 결제 금액, 결제 완료 상태(PAID), 처리 사장님 ID 기록
    //   5단계: 등록된 결제 정보 반환
    // =========================================================================



    // =========================================================================
    // 기능 6. [오기록 정정 (SELL-06)]
    // - 설명: 사장님이 실수로 잘못 누른 픽업/미수령 상태나 결제 기록을 각각 사유를 남겨 정정합니다.
    // - 규칙: 픽업·결제 중 하나를 정정해도 다른 기록이 임의 변경되지 않아야 함!
    //
    // - 처리 순서 A (예약 상태 오기록 정정):
    //   1단계: 대상 예약 정보를 자물쇠 걸고 조회
    //   2단계: 사장님 소유권 검증
    //   3단계: 정정 사유와 함께 목표 상태로 정정 (reservation.correctStatus 호출)
    //   4단계: 정정된 예약 결과 반환
    //
    // - 처리 순서 B (현장 결제 오기록 정정):
    //   1단계: 대상 예약 정보 조회 및 사장님 소유권 검증
    //   2단계: 해당 예약의 결제 기록(Payment) 조회
    //   3단계: 정정 사유와 함께 결제 수단, 금액, 결제 상태 정정 (payment.correctPayment 호출)
    //   4단계: 정정된 결제 결과 반환
    // =========================================================================

}

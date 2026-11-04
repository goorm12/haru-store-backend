package com.harustore.backend.domain.reservation.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.groupbuy.entity.GroupBuyStatus;
import com.harustore.backend.domain.groupbuy.repository.GroupBuyRepository;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.repository.MemberRepository;
import com.harustore.backend.domain.reservation.dto.ReservationCancelRequest;
import com.harustore.backend.domain.reservation.dto.ReservationCreateRequest;
import com.harustore.backend.domain.reservation.dto.ReservationQuantityUpdateRequest;
import com.harustore.backend.domain.reservation.dto.ReservationResponse;
import com.harustore.backend.domain.reservation.entity.CancelBy;
import com.harustore.backend.domain.reservation.entity.Reservation;
import com.harustore.backend.domain.reservation.entity.ReservationStatus;
import com.harustore.backend.domain.reservation.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * [구매자용 예약 서비스 (Buyer)]
 * - 예약 신청 및 즉시 확정 (RSV-01)
 * - 내 예약 목록 및 단건 상세 조회 (RSV-02)
 * - 예약 수량 변경 (RSV-03)
 * - 구매자 직접 예약 취소 (RSV-05, RSV-06)
 */
@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              GroupBuyRepository groupBuyRepository,
                              MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.groupBuyRepository = groupBuyRepository;
        this.memberRepository = memberRepository;
    }

    // =========================================================================
    // 기능 1. [예약 생성 (RSV-01)]
    // - 설명: 구매자가 상품과 수량을 선택해 예약을 신청하면 즉시 확정 예약을 생성합니다.
    // =========================================================================
    @Transactional
    public Long createReservation(Long memberId, ReservationCreateRequest request) {
        // [1단계] 대상 공구 정보를 가져오기
        GroupBuy groupBuy = groupBuyRepository.findByIdWithLock(request.getGroupBuyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // [2단계] 공구 상태 확인
        if (groupBuy.getStatus() != GroupBuyStatus.OPEN) {
            throw new BusinessException(ErrorCode.GROUP_BUY_NOT_OPEN);
        }

        // [3단계] 재고 확인
        if (groupBuy.getRemainingQuantity() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        // [4단계] 1인당 구매 제한 확인
        if (groupBuy.getMaxLimitPerUser() != null && groupBuy.getMaxLimitPerUser() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.EXCEED_MAX_LIMIT_PER_USER);
        }

        // [5단계] 중복 예약 여부 확인
        boolean isAlreadyReserved = reservationRepository.existsByBuyerIdAndGroupBuyIdAndStatus(memberId, groupBuy.getId(), ReservationStatus.RESERVED);

        if (isAlreadyReserved) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }

        // [6단계] 과거 수령 이력 확인
        boolean isAlreadyPickedUp = reservationRepository.existsByBuyerIdAndGroupBuyIdAndStatus(memberId, groupBuy.getId(), ReservationStatus.PICKED_UP);

        if (isAlreadyPickedUp) {
            throw new BusinessException(ErrorCode.ALREADY_PICKED_UP);
        }

        // [7단계] 구매자 회원 조회
        Member buyer = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // [8단계] 공구 재고 선점
        groupBuy.increaseReservedQuantity(request.getQuantity());

        // [9단계] 고유한 예약 번호 생성
        String reservationNumber = generateReservationNumber();

        // [10단계] 예약 당시 단가, 총금액, 픽업일, 상점 주소/운영시간, 예약자 정보 등을 스냅샷으로 담아 예약 데이터 저장
        Reservation reservation = new Reservation(
                reservationNumber,
                buyer,
                groupBuy,
                request.getQuantity(),
                groupBuy.getPrice(),
                groupBuy.getPickupStartDate(),
                groupBuy.getStore().getAddress(),
                groupBuy.getStore().getOperatingHours(),
                groupBuy.isCancelRestricted(),
                buyer.getName(),
                buyer.getPhoneNumber()
        );

        // [11단계] 생성된 예약 결과 반환
        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation.getId();
    }


    // =========================================================================
    // 기능 2. [내 예약 목록 및 단건 상세 조회 (RSV-02)]
    // - 설명: 로그인한 구매자가 본인의 예약 목록을 최신순으로 보거나, 특정 예약 1건을 상세 조회합니다.
    // =========================================================================
    public Page<ReservationResponse> getMyReservations(Long memberId, Pageable pageable) {
        // [1단계] 존재하는 회원인지 확인
        boolean isMember = memberRepository.existsById(memberId);
        if (!isMember) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // [2단계] 최신순 페이징 조회
        Page<Reservation> reservationPage = reservationRepository.findByBuyerId(memberId, pageable);

        // [3단계] 반환
        return reservationPage.map(ReservationResponse::new);
    }

    public ReservationResponse getMyReservation(Long memberId, Long reservationId) {
        // [1단계] 존재하는 회원인지 확인
        boolean isMember = memberRepository.existsById(memberId);
        if (!isMember) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // [2단계] 존재하는 예약인지 확인
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // [3단계] 본인 예약인지 소유권 확인
        if (!reservation.getBuyer().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "본인의 예약만 조회할 수 있습니다.");
        }

        // [4단계] DTO로 반환
        return new ReservationResponse(reservation);
    }


    // =========================================================================
    // 기능 3. [예약 수량 변경 (RSV-03)]
    // - 설명: 이미 완료된 예약의 수량을 늘리거나 줄입니다. (단가는 예약 당시 단가를 유지)
    // =========================================================================
    @Transactional
    public ReservationResponse updateReservationQuantity(
            Long memberId,
            Long reservationId,
            ReservationQuantityUpdateRequest request
    ) {
        // [1단계] 존재하는 회원인지 확인
        boolean isMember = memberRepository.existsById(memberId);
        if (!isMember) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // [2단계] 대상 예약 정보
        Reservation reservation = reservationRepository.findByIdWithLock(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // [3단계] 본인이 예약한것이 맞는지 확인
        if (!reservation.getBuyer().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "본인이 예약한 공구만 변경이 가능합니다.");
        }

        // [4단계] 현재 상태 확인
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS, "예약 완료 상태에서만 수량을 변경할 수 있습니다.");
        }

        // [5단계] 변경 가능 기한 확인
        LocalDate today = LocalDate.now();
        LocalDate pickupStartDate = reservation.getPickupStartDate();

        if (!today.isBefore(pickupStartDate)) {
            throw new BusinessException(ErrorCode.QUANTITY_REDUCE_DEADLINE_EXCEEDED, "픽업 시작일 당일부터는 구매자가 직접 수량을 변경할 수 없습니다. 판매자에게 문의해 주세요.");
        }

        int currentQuantity = reservation.getQuantity();
        int newQuantity = request.getNewQuantity();

        // 1. 수량이 그대로인 경우 (아무것도 안 함)
        if (newQuantity == currentQuantity) {
            return new ReservationResponse(reservation);
        }

        // 공구 정보 (자물쇠 걸고 조회)
        GroupBuy groupBuy = groupBuyRepository.findByIdWithLock(reservation.getGroupBuy().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // 2. 수량을 줄이는 경우
        if (newQuantity < currentQuantity) {
            if (reservation.isCancellationRestricted()) {
                throw new BusinessException(ErrorCode.CANCEL_RESTRICTED);
            }
            int diff = currentQuantity - newQuantity;
            groupBuy.decreaseReservedQuantity(diff);
        }
        // 3. 수량을 늘리는 경우
        else {
            int diff = newQuantity - currentQuantity;

            if (!groupBuy.getStatus().equals(GroupBuyStatus.OPEN)) {
                throw new BusinessException(ErrorCode.GROUP_BUY_NOT_OPEN);
            }

            if (groupBuy.getRemainingQuantity() < diff) {
                throw new BusinessException(ErrorCode.OUT_OF_STOCK);
            }

            Integer maxLimit = groupBuy.getMaxLimitPerUser();
            if (maxLimit != null && maxLimit < newQuantity) {
                throw new BusinessException(ErrorCode.EXCEED_MAX_LIMIT_PER_USER);
            }

            groupBuy.increaseReservedQuantity(diff);
        }

        reservation.changeQuantity(newQuantity);
        return new ReservationResponse(reservation);
    }


    // =========================================================================
    // 기능 4. [구매자 직접 예약 취소 (RSV-05, RSV-06)]
    // - 설명: 구매자가 본인의 예약을 직접 취소합니다.
    // =========================================================================
    @Transactional
    public ReservationResponse cancelReservationByBuyer(
            Long memberId,
            Long reservationId,
            ReservationCancelRequest request
    ) {
        // [1단계] 대상 예약 정보 가져오기
        Reservation reservation = reservationRepository.findByIdWithLock(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // [2단계] 본인 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if (!reservation.getBuyer().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // [3단계] 현재 상태 확인
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS, "예약 완료(RESERVED) 상태인 예약만 취소할 수 있습니다.");
        }

        // [4단계] 직접 취소 제한 품목 확인
        if (reservation.getGroupBuy().isCancelRestricted()) {
            throw new BusinessException(ErrorCode.CANCEL_RESTRICTED);
        }

        // [5단계] 취소 가능 기한 확인
        LocalDate today = LocalDate.now();
        LocalDate pickupStartDate = reservation.getPickupStartDate();

        if (!today.isBefore(pickupStartDate)) {
            throw new BusinessException(ErrorCode.CANCEL_DEADLINE_EXCEEDED, "픽업 시작일 당일부터는 구매자가 직접 취소할 수 없습니다. 판매자에게 문의해 주세요.");
        }

        // [6단계] 취소된 수량만큼 예약된 수량 감소
        Long groupBuyId = reservation.getGroupBuy().getId();
        GroupBuy groupBuy = groupBuyRepository.findByIdWithLock(groupBuyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));
        groupBuy.decreaseReservedQuantity(reservation.getQuantity());

        // [7단계] 예약 상태 취소로 변경 및 사유 추가
        reservation.cancel(request.getCancelReason(), CancelBy.BUYER);

        return new ReservationResponse(reservation);
    }


    // ==========================================
    // 헬퍼 메서드
    // ==========================================

    // 예약 주문 번호 생성
    private String generateReservationNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
        return "R" + datePart + "-" + randomPart;
    }
}

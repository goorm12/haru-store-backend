package com.harustore.backend.domain.reservation.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.groupbuy.entity.GroupBuyStatus;
import com.harustore.backend.domain.groupbuy.repository.GroupBuyRepository;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.repository.MemberRepository;
import com.harustore.backend.domain.reservation.dto.ReservationCreateRequest;
import com.harustore.backend.domain.reservation.entity.Reservation;
import com.harustore.backend.domain.reservation.entity.ReservationStatus;
import com.harustore.backend.domain.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

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
    // - 처리 순서:
    //   1단계: 대상 공구 정보를 가져옵니다. (동시성 방지를 위해 자물쇠를 걸고 조회)
    //   2단계: 공구 상태 확인 (현재 접수 중/모집 중인 상태인지 검증)
    //   3단계: 재고 확인 (남은 준비 수량이 예약 요청 수량 이상인지 검증)
    //   4단계: 1인당 구매 제한 확인 (공구에 1인당 최대 제한이 있다면 이를 넘지 않는지 검증)
    //   5단계: 중복 예약 여부 확인 (이 구매자가 해당 공구에 이미 진행 중인 예약이 있는지 검증)
    //   6단계: 과거 수령 완료 이력 확인 (과거에 이 공구를 이미 수령해 간 적이 있다면 재예약 차단)
    //   7단계: 구매자 회원 정보 조회
    //   8단계: 공구 재고 선점 (공구의 예약 수량을 이번 요청 수량만큼 증가)
    //   9단계: 고유한 예약 번호 생성
    //   10단계: 예약 당시 단가, 총금액, 픽업일, 상점 주소/운영시간, 예약자 정보 등을 스냅샷으로 담아 예약 데이터 저장
    //   11단계: 생성된 예약 결과 반환
    // =========================================================================
    @Transactional
    public Long createReservation(Long memberId, ReservationCreateRequest request) {
        // [1단계] 대상 공구 정보를 가져오기
        GroupBuy groupBuy = groupBuyRepository.findByIdWithLock(request.getGroupBuyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // [2단계] 공구 상태 확인
        if(groupBuy.getStatus() != GroupBuyStatus.OPEN) {
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

        if(isAlreadyReserved) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESERVATION);
        }

        // [6단계] 과거 수령 이력 확인
        boolean isAlreadyPickedUp = reservationRepository.existsByBuyerIdAndGroupBuyIdAndStatus(memberId, groupBuy.getId(), ReservationStatus.PICKED_UP);

        if(isAlreadyPickedUp) {
            throw new BusinessException(ErrorCode.ALREADY_PICKED_UP);
        }

        // [7단계] 구매자 회원 조회
        Member buyer = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        //   8단계: 공구 재고 선점
        groupBuy.increaseReservedQuantity(request.getQuantity());

        //   9단계: 고유한 예약 번호 생성
        String reservationNumber =  generateReservationNumber();

        //   10단계: 예약 당시 단가, 총금액, 픽업일, 상점 주소/운영시간, 예약자 정보 등을 스냅샷으로 담아 예약 데이터 저장
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

        //   11단계: 생성된 예약 결과 반환
        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation.getId();
    }


    // =========================================================================
    // 기능 2. [내 예약 목록 및 단건 상세 조회 (RSV-02)]
    // - 설명: 로그인한 구매자가 본인의 예약 목록을 최신순으로 보거나, 특정 예약 1건을 상세 조회합니다.
    // - 처리 순서 (목록 조회):
    //   1단계: 로그인한 구매자의 회원 식별자로 본인의 예약 전체를 최신순 페이징 조회
    //   2단계: 응답 데이터로 변환하여 반환
    //
    // - 처리 순서 (단건 상세 조회):
    //   1단계: 예약 식별자(ID) 또는 예약 번호로 예약 정보 단건 조회
    //   2단계: 본인 확인 (조회된 예약의 구매자가 현재 로그인한 본인이 맞는지 검증)
    //   3단계: 상세 예약 정보 반환
    // =========================================================================



    // =========================================================================
    // 기능 3. [예약 수량 변경 (RSV-03)]
    // - 설명: 이미 완료된 예약의 수량을 늘리거나 줄입니다. (단가는 예약 당시 단가를 유지)
    // - 처리 순서:
    //   1단계: 대상 예약 정보를 가져옵니다. (동시성 방지를 위해 자물쇠를 걸고 조회)
    //   2단계: 본인 확인 (본인이 예약한 건이 맞는지 검증)
    //   3단계: 현재 상태 확인 (이미 취소되었거나 수령 완료된 예약은 변경 불가)
    //   4단계: 변경 가능 기한 확인 (픽업 시작일 전날 밤 23:59:59까지만 변경 가능! 당일부터는 직접 변경 차단)
    //   5단계: 수량을 줄이는 경우
    //          - 직접 취소 제한 품목인지 확인 (취소 제한 품목이면 구매자 임의 감소 불가)
    //          - 줄어든 수량만큼 공구의 예약 수량 감소 (재고 반환)
    //   6단계: 수량을 늘리는 경우
    //          - 공구 장부를 자물쇠 걸고 가져와서 추가로 필요한 만큼 남은 재고가 있는지 검증
    //          - 1인당 구매 제한을 넘지 않는지 검증
    //          - 늘어난 수량만큼 공구의 예약 수량 증가 (추가 재고 선점)
    //   7단계: 예약 데이터의 수량 및 총금액(단가 × 변경된 수량) 갱신
    //   8단계: 변경된 예약 결과 반환
    // =========================================================================



    // =========================================================================
    // 기능 4. [구매자 직접 예약 취소 (RSV-05, RSV-06)]
    // - 설명: 구매자가 본인의 예약을 취소합니다. 기한이나 품목에 따라 직접 취소하거나 상점 문의로 유도합니다.
    // - 처리 순서:
    //   1단계: 대상 예약 정보를 가져옵니다. (동시성 방지를 위해 자물쇠를 걸고 조회)
    //   2단계: 본인 확인 (본인이 예약한 건이 맞는지 검증)
    //   3단계: 현재 상태 확인 (이미 취소되었거나 수령 완료된 예약은 취소 불가)
    //   4단계: 직접 취소 제한 품목 확인 (취소 제한 품목이면 구매자 직접 취소 차단)
    //   5단계: 취소 가능 기한 확인 (픽업 시작일 전날 밤 23:59:59까지만 직접 취소 가능! 당일부터는 직접 취소 차단 및 상점 문의 유도)
    //   6단계: 공구 장부를 자물쇠 걸고 가져와서 취소된 수량만큼 예약 수량 감소 (재고 반환)
    //   7단계: 예약 상태를 취소로 변경하고, 취소 사유, 취소 주체(구매자), 취소 일시 기록
    //   8단계: 취소 결과 반환
    // =========================================================================



    // 예약 주문 번호 생성
    private String generateReservationNumber() {
        // 1. 오늘 날짜를 YYMMDD 포맷으로 변환 (예: 261030)
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 2. 중복 방지용 랜덤 문자열 8자리 생성 (UUID)
        String randomPart = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();

        // 3. 조합
        return "R" + datePart + "-" + randomPart;


    }

}

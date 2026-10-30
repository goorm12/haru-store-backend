package com.harustore.backend.domain.groupbuy.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.groupbuy.dto.GroupBuyCreateRequest;
import com.harustore.backend.domain.groupbuy.dto.GroupBuyResponse;
import com.harustore.backend.domain.groupbuy.dto.GroupBuySummaryResponse;
import com.harustore.backend.domain.groupbuy.dto.GroupBuyUpdateRequest;
import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.groupbuy.entity.GroupBuyStatus;
import com.harustore.backend.domain.groupbuy.repository.GroupBuyRepository;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.repository.MemberRepository;
import com.harustore.backend.domain.store.entity.Store;
import com.harustore.backend.domain.store.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    public GroupBuyService(GroupBuyRepository groupBuyRepository,
                           StoreRepository storeRepository,
                           MemberRepository memberRepository) {
        this.groupBuyRepository = groupBuyRepository;
        this.storeRepository = storeRepository;
        this.memberRepository = memberRepository;
    }

    // ==========================================
    // 1. 공구 판매글 등록 (LIST-02)
    // ==========================================
    // [설명]
    // 판매자 본인 상점에 새로운 공동구매 판매글을 개설합니다. (임시저장 DRAFT 또는 즉시공개 OPEN)
    // [비즈니스 규칙 & 검증]
    // 1. 회원 및 상점 존재 검증
    // 2. 판매자 소유권 검증: 요청한 회원이 이 상점의 판매자가 맞는지 확인 (아니면 ACCESS_DENIED)
    // 3. 엔티티 생성 및 저장 후 생성된 공구 ID 반환
    @Transactional
    public Long createGroupBuy(Long memberId, GroupBuyCreateRequest request) {
        // [1단계] 회원 존재 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // [2단계] 상점 존재 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // [3단계] 판매자 본인 소유권 검증 -> 이 상점의 판매자가 맞는지 확인
        if (!store.getSeller().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 상점의 판매자만 공구를 등록할 수 있습니다.");
        }

        // [4단계] DTO의 정보들로 GroupBuy 엔티티 생성
        GroupBuy groupBuy = new GroupBuy(
                store,
                request.getTitle(),
                request.getDescription(),
                request.getImageUrl(),
                request.getUnit(),
                request.getPrice(),
                request.getTotalQuantity(),
                request.getMaxLimitPerUser(),
                request.getPickupStartDate(),
                request.getCancelRestricted() != null ? request.getCancelRestricted() : false,
                request.getStatus() != null ? request.getStatus() : GroupBuyStatus.OPEN
        );

        // [5단계] DB에 저장하고, 저장된 객체에서 ID를 꺼내 반환
        GroupBuy savedGroupBuy = groupBuyRepository.save(groupBuy);
        return savedGroupBuy.getId();
    }

    // ==========================================
    // 2. 공구 정보 부분 수정 (LIST-03)
    // ==========================================
    // [설명]
    // 사장님이 등록된 공구의 정보(제목, 설명, 단가, 수량, 픽업일 등)를 부분 수정(PATCH)합니다.
    // [비즈니스 규칙 & 검증]
    // 1. 공구 존재 확인 및 사장님 본인 소유권 검증 (아니면 ACCESS_DENIED)
    // 2. ⚠️ 핵심 정합성 규칙: 총 수량(totalQuantity)을 수정할 경우,
    //    이미 손님들이 예약한 수량(reservedQuantity)보다 작게 줄일 수 없음! (위반 시 INVALID_INPUT_VALUE)
    // 3. patchInfo() 호출을 통한 더티 체킹 자동 UPDATE
    @Transactional
    public Long updateGroupBuy(Long memberId, Long groupBuyId, GroupBuyUpdateRequest request) {
        // [1단계] 공구 존재 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // [2단계] 사장님 본인 소유권 검증
        if (!groupBuy.getStore().getSeller().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 상점의 판매자만 공구를 수정할 수 있습니다.");
        }

        // [3단계] 총 수량을 수정할 경우, 이미 예약된 수량보다 작게 줄일 수 없음
        if (request.getTotalQuantity() != null && request.getTotalQuantity() < groupBuy.getReservedQuantity()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "총 모집 수량은 이미 예약된 수량보다 작게 줄일 수 없습니다.");
        }

        // [4단계] 엔티티의 patchInfo 호출하여 변경사항 반영
        groupBuy.patchInfo(
                request.getTitle(),
                request.getDescription(),
                request.getImageUrl(),
                request.getUnit(),
                request.getPrice(),
                request.getTotalQuantity(),
                request.getMaxLimitPerUser(),
                request.getPickupStartDate(),
                request.getCancelRestricted()
        );

        // [5단계] 수정된 공구 ID 반환
        return groupBuy.getId();
    }

    // ==========================================
    // 3. 판매자 수동 접수 마감 (LIST-04)
    // ==========================================
    // [설명]
    // 수량 오입력, 개인 사정, 조기 완판 등으로 사장님이 직접 신규 예약을 즉시 차단합니다.
    // [비즈니스 규칙 & 검증]
    // 1. 공구 존재 확인 및 판매자 소유권 검증
    // 2. 상태를 CLOSED(마감)로 변경 (기존에 들어온 예약은 유지됨)
    @Transactional
    public Long closeGroupBuy(Long memberId, Long groupBuyId) {
        // [1단계] 공구 존재 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // [2단계] 사장님 소유권 검증
        if (!groupBuy.getStore().getSeller().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 상점의 판매자만 변경할 수 있습니다.");
        }

        // [3단계] 상태 검증 : OPEN 상태가 아니면 차단
        if (groupBuy.getStatus() != GroupBuyStatus.OPEN) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "접수 중(OPEN)인 공구만 마감할 수 있습니다.");
        }

        // [4단계] 상태를 CLOSED로 변경
        groupBuy.updateStatus(GroupBuyStatus.CLOSED);

        return groupBuy.getId();
    }

    // ==========================================
    // 4. 판매자 접수 재개 (LIST-04)
    // ==========================================
    // [설명]
    // 사정상 마감했던 공구를 다시 접수 중(OPEN) 상태로 되돌립니다.
    // [비즈니스 규칙 & 검증]
    // 1. 공구 존재 확인 및 사장님 소유권 검증
    // 2. 상태를 OPEN(접수중)으로 변경
    @Transactional
    public Long reopenGroupBuy(Long memberId, Long groupBuyId) {
        // [1단계] 공구 존재 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // [2단계] 사장님 소유권 검증
        if (!groupBuy.getStore().getSeller().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 상점의 판매자만 변경할 수 있습니다.");
        }

        // [3단계] 상태 검증 : CLOSED 상태가 아니면 차단
        if (groupBuy.getStatus() != GroupBuyStatus.CLOSED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "마감(CLOSED) 상태인 공구만 접수를 재개할 수 있습니다.");
        }

        // [4단계] 수량 확인 : 남은 수량이 있어야 함.
        if (groupBuy.getRemainingQuantity() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "남은 잔여 수량이 없어 재개할 수 없습니다. 총 수량을 먼저 늘려주세요.");
        }

        // [5단계] 상태 업데이트 및 ID 반환
        groupBuy.updateStatus(GroupBuyStatus.OPEN);
        return groupBuy.getId();
    }

    // ==========================================
    // 5. 판매자 공구 전체 취소 (LIST-05)
    // ==========================================
    // [설명]
    // 사장님의 부득이한 사정으로 공구 전체를 취소 처리합니다.
    // [비즈니스 규칙 & 검증]
    // 1. 공구 존재 확인 및 사장님 소유권 검증
    // 2. 상태를 CANCELLED(취소)로 변경
    @Transactional
    public Long cancelGroupBuy(Long memberId, Long groupBuyId) {
        // [1단계] 공구 존재 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // [2단계] 판매자 소유권 검증
        if (!groupBuy.getStore().getSeller().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 상점의 판매자만 변경할 수 있습니다.");
        }

        // [3단계] 상태 검증 : OPEN, CLOSED 상태만 가능
        if (groupBuy.getStatus() == GroupBuyStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 취소된 공구입니다.");
        }

        if (groupBuy.getStatus() == GroupBuyStatus.DRAFT) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "작성 중인 공구는 취소할 수 없습니다.");
        }

        // [4단계] OPEN 이거나 CLOSED 상태인 공구는 정상적으로 변경 가능
        groupBuy.updateStatus(GroupBuyStatus.CANCELLED);
        return groupBuy.getId();
    }

    // ==========================================
    // 6. 공구 단건 상세 조회 (LIST-01)
    // ==========================================
    // [설명]
    // 특정 공구의 전체 상세 정보(본문 설명 description 포함)를 조회합니다.
    // [비즈니스 규칙 & 검증]
    // 1. 공구 존재 확인 (없으면 GROUP_BUY_NOT_FOUND)
    // 2. 일반 구매자가 조회할 때 작성중(DRAFT)인 공구는 조회 불가 (비공개 처리)
    // 3. GroupBuyResponse 반환
    public GroupBuyResponse getGroupBuy(Long groupBuyId) {
        // [1단계] 공구 존재 확인
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND));

        // [2단계] DRAFT(임시저장) 상태인 공구는 일반인에게 비공개 처리
        if (groupBuy.getStatus() == GroupBuyStatus.DRAFT) {
            throw new BusinessException(ErrorCode.GROUP_BUY_NOT_FOUND);
        }

        // [3단계] DTO로 변환하여 반환
        return new GroupBuyResponse(groupBuy);
    }

    // ==========================================
    // 7. 공개 공구 목록 페이징 둘러보기 (LIST-01)
    // ==========================================
    // [설명]
    // 홈 화면이나 둘러보기 탭에서 현재 접수 중(OPEN)인 공개 공구 목록을 페이징 조회합니다.
    public Page<GroupBuySummaryResponse> getGroupBuys(String keyword, Pageable pageable) {
        Page<GroupBuy> groupBuyPage;

        if (keyword != null && !keyword.isBlank()) {
            groupBuyPage = groupBuyRepository.findByStatusAndTitleContaining(GroupBuyStatus.OPEN, keyword.trim(), pageable);
        } else {
            groupBuyPage = groupBuyRepository.findByStatus(GroupBuyStatus.OPEN, pageable);
        }

        return groupBuyPage.map(GroupBuySummaryResponse::new);
    }

    // ==========================================
    // 8. 특정 상점의 공구 목록 페이징 조회
    // ==========================================
    // [설명]
    // 특정 가게 상세 페이지에서 해당 가게가 진행 중인 공구 목록을 모아서 보여줍니다.
    public Page<GroupBuySummaryResponse> getStoreGroupBuys(Long storeId, Pageable pageable) {
        // [1단계] 상점 존재 검증
        if (!storeRepository.existsById(storeId)) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }

        // [2단계] 해당 상점의 접수 중인 공구들만 페이징 조회
        Page<GroupBuy> groupBuyPage = groupBuyRepository.findByStoreIdAndStatus(storeId, GroupBuyStatus.OPEN, pageable);

        // [3단계] Summary DTO로 변환
        return groupBuyPage.map(GroupBuySummaryResponse::new);
    }
}

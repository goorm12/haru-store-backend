package com.harustore.backend.domain.groupbuy.controller;

import com.harustore.backend.common.response.ApiResponse;
import com.harustore.backend.common.response.PageResponse;
import com.harustore.backend.domain.groupbuy.dto.GroupBuyCreateRequest;
import com.harustore.backend.domain.groupbuy.dto.GroupBuyResponse;
import com.harustore.backend.domain.groupbuy.dto.GroupBuySummaryResponse;
import com.harustore.backend.domain.groupbuy.dto.GroupBuyUpdateRequest;
import com.harustore.backend.domain.groupbuy.service.GroupBuyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-buys")
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    public GroupBuyController(GroupBuyService groupBuyService) {
        this.groupBuyService = groupBuyService;
    }

    // ==========================================
    // TODO 1. [공구 판매글 등록] POST /api/group-buys?memberId=1
    // ==========================================
    // - 입력: @RequestParam Long memberId, @Valid @RequestBody GroupBuyCreateRequest request
    // - 역할: 사장님이 본인 상점에 새로운 공구 판매글 개설 (DRAFT 또는 OPEN)
    // - 응답: ApiResponse<Long> ("공구 판매글이 등록되었습니다.", 생성된 groupBuyId)
    @PostMapping
    public ApiResponse<Long> createGroupBuy(
            @RequestParam Long memberId,
            @Valid @RequestBody GroupBuyCreateRequest request) {
        Long groupBuyId = groupBuyService.createGroupBuy(memberId, request);
        return ApiResponse.success("공구 판매글이 등록되었습니다.", groupBuyId);
    }
    // ==========================================
    // TODO 2. [공구 정보 부분 수정] PATCH /api/group-buys/{groupBuyId}?memberId=1
    // ==========================================
    // - 입력: @PathVariable Long groupBuyId, @RequestParam Long memberId, @Valid @RequestBody GroupBuyUpdateRequest request
    // - 역할: 사장님이 공구 정보(단가, 수량, 제목 등)를 부분 수정 (더티 체킹)
    // - 응답: ApiResponse<Long> ("공구 정보가 수정되었습니다.", groupBuyId)
    @PatchMapping("/{groupBuyId}")
    public ApiResponse<Long> patchGroupBuyInfo(
            @RequestParam Long memberId,
            @PathVariable Long groupBuyId,
            @Valid @RequestBody GroupBuyUpdateRequest request) {
        Long updatedId  = groupBuyService.updateGroupBuy(memberId, groupBuyId, request);
        return ApiResponse.success("공구 정보가 수정되었습니다.",  updatedId);
    }


    // ==========================================
    // TODO 3. [판매자 수동 접수 마감] PATCH /api/group-buys/{groupBuyId}/close?memberId=1
    // ==========================================
    // - 입력: @PathVariable Long groupBuyId, @RequestParam Long memberId
    // - 역할: 사장님이 진행 중(OPEN)인 공구를 수동으로 마감(CLOSED) 처리
    // - 응답: ApiResponse<Long> ("공구 접수가 마감되었습니다.", groupBuyId)
    @PatchMapping("/{groupBuyId}/close")
    public ApiResponse<Long> closeGroupBuy(
            @RequestParam Long memberId,
            @PathVariable Long groupBuyId
    ) {
        Long closedId = groupBuyService.closeGroupBuy(memberId, groupBuyId);
        return ApiResponse.success("공구 마감 설정이 완료되었습니다.", closedId);
    }


    // ==========================================
    // TODO 4. [판매자 접수 재개] PATCH /api/group-buys/{groupBuyId}/reopen?memberId=1
    // ==========================================
    // - 입력: @PathVariable Long groupBuyId, @RequestParam Long memberId
    // - 역할: 마감(CLOSED) 상태인 공구를 다시 접수 중(OPEN) 상태로 재개
    // - 응답: ApiResponse<Long> ("공구 접수가 재개되었습니다.", groupBuyId)
    @PatchMapping("/{groupBuyId}/reopen")
    public ApiResponse<Long> reopenGroupBuy(
            @RequestParam Long memberId,
            @PathVariable Long groupBuyId
    ) {
        Long reopenedId = groupBuyService.reopenGroupBuy(memberId, groupBuyId);
        return ApiResponse.success("공구 접수가 재개되었습니다.", reopenedId);
    }


    // ==========================================
    // TODO 5. [판매자 공구 전체 취소] PATCH /api/group-buys/{groupBuyId}/cancel?memberId=1
    // ==========================================
    // - 입력: @PathVariable Long groupBuyId, @RequestParam Long memberId
    // - 역할: 부득이한 사정으로 공구를 전체 취소(CANCELLED) 처리
    // - 응답: ApiResponse<Long> ("공구가 전체 취소되었습니다.", groupBuyId)
    @PatchMapping("/{groupBuyId}/cancel")
    public ApiResponse<Long> cancelGroupBuy(
            @RequestParam Long memberId,
            @PathVariable Long groupBuyId
    ) {
        Long cancelledId = groupBuyService.cancelGroupBuy(memberId, groupBuyId);
        return ApiResponse.success("공구가 전체 취소되었습니다.", cancelledId);
    }


    // ==========================================
    // TODO 6. [공구 단건 상세 조회] GET /api/group-buys/{groupBuyId}
    // ==========================================
    // - 입력: @PathVariable Long groupBuyId (비회원/구매자 모두 조회 가능)
    // - 역할: 특정 공구의 상세 정보(긴 본문 설명 description 포함) 단건 조회
    // - 응답: ApiResponse<GroupBuyResponse> ("공구 상세 조회가 완료되었습니다.", response)
    @GetMapping("/{groupBuyId}")
    public ApiResponse<GroupBuyResponse> getGroupBuy(
            @PathVariable Long groupBuyId
    ) {
        GroupBuyResponse groupBuyResponse = groupBuyService.getGroupBuy(groupBuyId);
        return ApiResponse.success("공구 상세 조회가 완료되었습니다.", groupBuyResponse);
    }


    // ==========================================
    // TODO 7. [공개 공구 목록 둘러보기 & 검색] GET /api/group-buys?keyword=사과&page=0&size=10
    // ==========================================
    // - 입력: @RequestParam(required = false) String keyword, @PageableDefault(...) Pageable pageable
    // - 역할: 현재 접수 중(OPEN)인 공개 공구 목록을 페이징 조회 (가벼운 Summary DTO)
    // - 응답: ApiResponse<PageResponse<GroupBuySummaryResponse>> ("공구 목록 조회가 완료되었습니다.", PageResponse.of(...))
    @GetMapping
    public ApiResponse<PageResponse<GroupBuySummaryResponse>> getGroupBuys(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<GroupBuySummaryResponse> response = groupBuyService.getGroupBuys(keyword, pageable);
        return ApiResponse.success("공구 목록 조회가 완료되었습니다.",  PageResponse.from(response));
    }

    // ==========================================
    // TODO 8. [특정 상점의 공구 목록 조회] GET /api/group-buys/stores/{storeId}?page=0&size=10
    // ==========================================
    // - 입력: @PathVariable Long storeId, @PageableDefault(...) Pageable pageable
    // - 역할: 특정 상점 상세 페이지에서 해당 상점의 진행 중(OPEN) 공구 목록 페이징 조회
    // - 응답: ApiResponse<PageResponse<GroupBuySummaryResponse>> ("상점 공구 목록 조회가 완료되었습니다.", PageResponse.of(...))
    @GetMapping("/stores/{storeId}")
    public ApiResponse<PageResponse<GroupBuySummaryResponse>> getStoreGroupBuys(
            @PathVariable Long storeId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<GroupBuySummaryResponse> response = groupBuyService.getStoreGroupBuys(storeId, pageable);
        return ApiResponse.success("상점 공구 목록 조회가 완료되었습니다.", PageResponse.from(response));
    }
}

package com.harustore.backend.domain.store.controller;

import com.harustore.backend.common.response.ApiResponse;
import com.harustore.backend.common.response.PageResponse;
import com.harustore.backend.domain.store.dto.StoreCreateRequest;
import com.harustore.backend.domain.store.dto.StoreResponse;
import com.harustore.backend.domain.store.dto.StoreUpdateRequest;
import com.harustore.backend.domain.store.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

   private final StoreService storeService;

   public StoreController(StoreService storeService) {
       this.storeService = storeService;
   }

    // 1. 상점 등록
    @PostMapping
    public ApiResponse<Long> createStore(@Valid @RequestBody StoreCreateRequest request) {
       Long storeId = storeService.createStore(request);
       return ApiResponse.success("상점이 성공적으로 등록되었습니다.",storeId);
    }
    // 2. 상점 단건 조회(상세 조회)
    @GetMapping("/{storeId}")
    public ApiResponse<StoreResponse> getStore(@PathVariable Long storeId) {
       StoreResponse response = storeService.getStore(storeId);
       return ApiResponse.success("상점 상세 정보 조회가 완료되었습니다.",response);
    }

    // 3. 상점 검색 및 목록 조회(페이징)
    @GetMapping
    public ApiResponse<PageResponse<StoreResponse>> getStores(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size=10, page=0, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<StoreResponse> pageResult  = storeService.searchStore(keyword, pageable);
        return ApiResponse.success("상점 목록 조회가 완료되었습니다.", PageResponse.from(pageResult));
    }

    // 4. 상점 수정
    @PatchMapping("/{storeId}")
    public ApiResponse<StoreResponse> updateStore(
            @PathVariable Long storeId,
            @RequestParam Long memberId,
            @Valid @RequestBody StoreUpdateRequest request
    ) {
       StoreResponse response = storeService.updateStore(storeId, memberId, request);

       return ApiResponse.success("상점 수정이 완료되었습니다.", response);
    }

    // 5. [판매자용] 본인이 소유한 상점 목록 전체 조회
    @GetMapping("/mine")
    public ApiResponse<List<StoreResponse>> getMyStores(@RequestParam Long sellerId) {
       List<StoreResponse> response = storeService.getMyStores(sellerId);
       return  ApiResponse.success("내 상점 목록 조회가 완료되었습니다.",response);
    }

}

package com.harustore.backend.domain.store.controller;

import com.harustore.backend.common.response.ApiResponse;
import com.harustore.backend.domain.store.dto.FavoriteStoreResponse;
import com.harustore.backend.domain.store.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class FavoriteStoreController {

    private final FavoriteService favoriteService;
    public FavoriteStoreController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }


    // 단골 등록
    @PostMapping("/{storeId}/favorite")
    public ApiResponse<Boolean> addFavorite(@RequestParam Long memberId, @PathVariable Long storeId){
        boolean isFavorite = favoriteService.toggleFavorite(memberId, storeId);

        String message = isFavorite ? "단골 등록이 완료되었습니다.":"단골 등록이 해제되었습니다.";
        return ApiResponse.success(message,isFavorite);
    }

    /* 대표상점 등록
     * 기존 대표상점이 있다면, 해제하고 현재 요청 상점을 대표 상점으로 교체
     * 찜도 안되어있다면, 찜 + 대표 상점 등록
     */
    @PostMapping("/{storeId}/default")
    public ApiResponse<Boolean> setDefaultStore(@RequestParam Long memberId, @PathVariable Long storeId){
        boolean result = favoriteService.setDefaultStore(memberId, storeId);
        return ApiResponse.success("기본 상점으로 지정되었습니다.", result);
    }


    // 단골 상점 조회
    @GetMapping("/favorites")
    public ApiResponse<List<FavoriteStoreResponse>> getFavoriteStores(@RequestParam Long memberId){
        List<FavoriteStoreResponse> response = favoriteService.getFavoriteStores(memberId);
        return ApiResponse.success("단골 상점 조회가 완료되었습니다.", response);
    }


}

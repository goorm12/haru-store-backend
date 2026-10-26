package com.harustore.backend.domain.store.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.repository.MemberRepository;
import com.harustore.backend.domain.store.dto.FavoriteStoreResponse;
import com.harustore.backend.domain.store.entity.FavoriteStore;
import com.harustore.backend.domain.store.entity.Store;
import com.harustore.backend.domain.store.repository.FavoriteStoreRepository;
import com.harustore.backend.domain.store.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteStoreRepository favoriteStoreRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    public FavoriteService(FavoriteStoreRepository favoriteStoreRepository, MemberRepository memberRepository, StoreRepository storeRepository) {
        this.favoriteStoreRepository = favoriteStoreRepository;
        this.memberRepository = memberRepository;
        this.storeRepository = storeRepository;
    }

    /**
     * 상점 찜 토글 (단골 등록 / 해제)
     * @return true: 찜 등록됨, false: 찜 해제됨
     */
    @Transactional
    public boolean toggleFavorite(Long memberId, Long storeId) {
        // 1. 이미 찜한 내역이 있는지 조회
        Optional<FavoriteStore> favoriteOpt = favoriteStoreRepository.findByStoreIdAndMemberId(storeId,memberId);


        // 2. 이미 등록되어 있다면 -> 찜 해제 (삭제)
       if(favoriteOpt.isPresent()) {
           favoriteStoreRepository.delete(favoriteOpt.get());
           return false;
       }


        // 3. 등록되어 있지 않다면 -> 신규 찜 등록
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 회원입니다."));

       Store store = storeRepository.findById(storeId)
               .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 상점입니다."));


       favoriteStoreRepository.save(new FavoriteStore(member, store, false));
        return true; // 찜 등록 상태 반환
    }

    // 대표 상점 지정
    @Transactional
    public boolean setDefaultStore(Long memberId, Long storeId) {
        // 대표 상점 찾기
        Optional<FavoriteStore> currentDefaultOpt = favoriteStoreRepository.findByMemberIdAndIsDefaultTrue(memberId);

        // 해당 대표 상점을 false로 변경하기
        currentDefaultOpt.ifPresent(currentDefault -> currentDefault.updateDefault(false));


        // 해당 가게가 현재 찜 목록에 있는지 확인
        Optional<FavoriteStore> favoriteStoreOpt = favoriteStoreRepository.findByStoreIdAndMemberId(storeId,memberId);

        // 이미 되어있다면? 대표 상점 등록
        if(favoriteStoreOpt.isPresent()) {
            FavoriteStore favoriteStore = favoriteStoreOpt.get();
            favoriteStore.updateDefault(true);
            return true;
        }
        // 찜이 안되어있다면 찜등록해주고, 대표 상점 등록
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 회원입니다."));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 상점입니다."));


        // 대표 상점으로 새로 생성해서 DB에 저장
        favoriteStoreRepository.save(new FavoriteStore(member, store, true));

        return true;
    }

    // 단골 상점 목록 조회

    public List<FavoriteStoreResponse> getFavoriteStores(Long memberId) {
       memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,"존재하지 않는 회원입니다."));


        return favoriteStoreRepository.findByMemberId(memberId)
                .stream()
                .map(FavoriteStoreResponse::new)
                .toList();


    }

}

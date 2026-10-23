package com.harustore.backend.domain.store.repository;

import com.harustore.backend.domain.store.entity.FavoriteStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteStoreRepository extends JpaRepository<FavoriteStore, Long> {
    // TODO 1. [내 단골 목록] 특정 구매자가 찜한 단골 상점 전체 목록 조회
    // 목적: 마이페이지에서 내가 찜한 가게들을 한 번에 모아서 보여줄 때 사용
    List<FavoriteStore> findByMemberId(Long memberId);

    // TODO 2. [중복 방지] 특정 회원이 특정 상점을 이미 찜했는지 존재 여부 확인
    // 목적: 중복 찜을 방지하기 위해 이미 등록되어 있는지 true/false로 체크할 때 사용
    boolean existsByStoreIdAndMemberId(Long storeId, Long memberId);

    // TODO 3. [단골 취소] 특정 회원이 찜한 특정 상점의 찜 데이터 단건 조회
    // 목적: 찜을 해제(삭제)하기 위해 그 찜 내역을 DB에서 가져올 때 사용
    Optional<FavoriteStore> findByStoreIdAndMemberId(Long storeId, Long memberId);

    // TODO 4. [기본 상점 변경] 특정 회원이 현재 '기본 상점'으로 지정해둔 단골 데이터 조회
    // 목적: 새 가게를 기본 상점으로 지정할 때, 기존 기본 상점을 찾아서 해제(false)하기 위해 사용
    Optional<FavoriteStore> findByMemberIdAndIsDefaultTrue(Long memberId);
}

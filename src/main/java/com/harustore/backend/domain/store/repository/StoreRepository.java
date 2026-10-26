package com.harustore.backend.domain.store.repository;

import com.harustore.backend.domain.store.entity.Store;
import com.harustore.backend.domain.store.entity.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // TODO 1. [사장님용] 본인(판매자)이 소유한 상점 목록 전체 조회
    // 💡 힌트: seller_id를 조건으로 조회 (findBySellerId, 반환 타입: List<Store>)
    List<Store> findBySellerIdOrderByCreatedAtDesc(Long sellerId);


    // TODO 2. [구매자용] 정상 운영(ACTIVE) 중이면서 상점명에 검색어가 포함된 상점 검색
    // 💡 힌트: status와 name의 Containing 조건을 AND로 연결 (findByStatusAndNameContaining, 반환 타입: List<Store>)
    Page<Store> findByStatusAndNameContaining(StoreStatus Status, String name, Pageable pageable);

    // TODO 3. [구매자용] 검색어 없이 둘러보기 시 정상 운영(ACTIVE) 중인 상점 목록 전체 조회
    // 💡 힌트: status 컬럼이 일치하는 목록 조회 (findByStatus, 반환 타입: List<Store>)
    Page<Store> findByStatus(StoreStatus status, Pageable pageable);
}

package com.harustore.backend.domain.groupbuy.repository;

import com.harustore.backend.domain.groupbuy.entity.GroupBuy;
import com.harustore.backend.domain.groupbuy.entity.GroupBuyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBuyRepository extends JpaRepository<GroupBuy,Long> {
    // TODO 1. [구매자용 둘러보기] 특정 상태(status)의 공구 목록 전체 페이징 조회
    // 홈 화면이나 둘러보기 탭에서 현재 접수 중(OPEN)인 공구들을 모아서 보여줄 때 사용
    Page<GroupBuy> findByStatus(GroupBuyStatus status, Pageable pageable);

    // TODO 2. [사장님 관리용] 특정 상점(storeId)의 모든 공구 목록 페이징 조회
    // 사장님이 본인 상점 관리 페이지에서 작성중/접수중/마감 등 모든 공구를 확인할 때 사용
    Page<GroupBuy> findByStoreId(Long storeId, Pageable pageable);

    // TODO 3. [상점 상세용] 특정 상점(storeId)의 특정 상태(status) 공구 목록 페이징 조회
    // 특정 상점 상세 페이지에서 해당 가게가 현재 진행 중(OPEN)인 공구들만 모아서 보여줄 때 사용
    Page<GroupBuy> findByStoreIdAndStatus(Long storeId, GroupBuyStatus status, Pageable pageable);

    // TODO 4. [검색용] 공구 제목(title) 키워드 검색 및 상태별 페이징 조회
    // 손님이 상품명을 검색했을 때 키워드가 포함된 접수 중(OPEN) 공구들을 찾을 때 사용
    Page<GroupBuy> findByStatusAndTitleContaining(GroupBuyStatus status, String title, Pageable pageable);
}

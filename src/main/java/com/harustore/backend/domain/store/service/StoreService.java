package com.harustore.backend.domain.store.service;

import com.harustore.backend.common.exception.BusinessException;
import com.harustore.backend.common.exception.ErrorCode;
import com.harustore.backend.domain.member.entity.Member;
import com.harustore.backend.domain.member.repository.MemberRepository;
import com.harustore.backend.domain.store.dto.StoreCreateRequest;
import com.harustore.backend.domain.store.dto.StoreResponse;
import com.harustore.backend.domain.store.dto.StoreUpdateRequest;
import com.harustore.backend.domain.store.entity.Store;
import com.harustore.backend.domain.store.entity.StoreStatus;
import com.harustore.backend.domain.store.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    public StoreService(StoreRepository storeRepository, MemberRepository memberRepository) {
        this.storeRepository = storeRepository;
        this.memberRepository = memberRepository;
    }

    // 1. 상점 등록
    @Transactional
    public Long createStore(StoreCreateRequest request) {

        // [1단계] 회원 조회
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 회원입니다."));

        // [2단계] 회원을 판매자로 변경
        member.promoteToSeller();

        // [3단계] 찾은 회원(member)을 사장님으로 지정해서 상점 생성
        Store store = new Store(
                member,
                request.getName(),
                request.getAddress(),
                request.getPhoneNumber(),
                request.getOperatingHours(),
                request.getDescription()
        );

        // [4단계] 상점 저장 후 생성된 상점 ID 반환
        Store savedStore = storeRepository.save(store);
        return savedStore.getId();
    }

    // 2. 상점 단건 조회(상세 조회)
    public StoreResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 상점입니다."));

        return new StoreResponse(store);
    }

    // 3. 상점 검색 및 목록 (오프셋 페이징)
    public Page<StoreResponse> searchStore(String keyword, Pageable pageable) {
        Page<Store> storePage; // 나중에 결과 담을 바구니

        // 검색어가 있으면 키워드 검색, 없으면 정상 상점 전체 조회
        if (keyword != null && !keyword.trim().isEmpty()) {
            storePage = storeRepository.findByStatusAndNameContaining(StoreStatus.ACTIVE, keyword.trim(), pageable);
        } else {
            storePage = storeRepository.findByStatus(StoreStatus.ACTIVE, pageable);
        }

        /*
         * [Page<Store> 엔티티를 Page<StoreResponse> DTO로 변환하는 여러 가지 방식]
         *
         * 💡 map()의 원리:
         * "전체 페이지 수", "현재 페이지 번호" 등 페이징 메타데이터는 그대로 보존하면서,
         * 상자 안에 들어있는 내용물(Store 엔티티)들만 하나씩 꺼내어 DTO로 변환해 줍니다.
         *
         * [방식 1] 람다식(Lambda)으로 풀어서 쓴 형태 (가장 직관적인 표현)
         * -> return storePage.map(store -> new StoreResponse(store));
         *
         * [방식 2] 메서드 참조(Method Reference)로 축약한 형태 (실무에서 가장 많이 쓰는 표현)
         * -> return storePage.map(StoreResponse::new);
         *
         * [방식 3] 고전적인 for문 방식 (참고용 - 코드가 길고 페이징 정보를 재조립해야 해서 잘 안 씀)
         * -> List<StoreResponse> list = storePage.getContent().stream().map(StoreResponse::new).toList();
         *    return new PageImpl<>(list, pageable, storePage.getTotalElements());
         */
        return storePage.map(StoreResponse::new);
    }

    // 4. 상점 정보 수정
    @Transactional
    public StoreResponse updateStore(Long storeId, Long memberId, StoreUpdateRequest request) {
        // 상점 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,"존재하지 않는 상점입니다."));

        // 소유자 검증
        if(!store.getSeller().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,"본인이 개설한 상점만 수정할 수 있습니다.");
        }

        // 상점 업데이트
        store.patchInfo(
                request.getName(),
                request.getAddress(),
                request.getPhoneNumber(),
                request.getOperatingHours(),
                request.getDescription()
        );

        // 반환
        // @Transactional을 쓰면 JPA가 자동으로 save를 처리해주기때문에
        return new StoreResponse(store);
    }

    // 5. 판매자 본인 상점 목록 조회
    public List<StoreResponse> getMyStores(Long sellerId) {
        List<Store> stores =  storeRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);

        return stores.stream()          // 1. 컨베이어 벨트에 올리기
                .map(StoreResponse::new) // 2. 포장 상자에 담기
                .toList();         // 3. 새 바구니에 모아 담기
    }
}

package com.harustore.backend.domain.member.repository;

import com.harustore.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Member 엔티티의 데이터베이스 접근을 담당하는 JPA 리포지토리 인터페이스
 * JpaRepository<entity이름, PK타입>
 * Spring Data JPA의 "쿼리 메서드(Query Method)" 규칙
 * 1. 기본 공식: findBy + 필드명(첫 글자 대문자) findByPhoneNumber
 * 2. 조건이 2개 이상일 때: And / Or findByNameAndPhoneNumber,findByNameOrKakaoId
 * 3. 조회 말고 "있는지 없는지(true/false)" existsBy existsByKakaoId(String kakaoId)
 * 4. 검색창 만들 때 (LIKE 검색): Containing findByNameContaining
 * 5. 정렬할 때: OrderBy findAllByOrderByCreatedAtDesc()
 */
public interface MemberRepository extends JpaRepository<Member,Long> {

    // 1. 카카오 고유 ID로 회원 단건 조회 (로그인할 때 사용)
    // 회원이 없을 수도 있으므로 null 방지를 위해 Optional로 감쌉니다.
    Optional<Member> findByKakaoId(String kakaoId);

    // 2. 이미 가입된 카카오 ID인지 중복 여부 확인 (회원가입할 때 검사용)
    boolean existsByKakaoId(String kakaoId);
}

package com.harustore.backend.domain.member.entity;

import com.harustore.backend.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity // 1. JPA가 관리하는 엔티티임을 선언
@Table(name="member") // 2. DB 테이블 이름을 member로 지정
public class Member extends BaseEntity {
    // 💡 extends BaseEntity를 썼기 때문에,
    // created_at과 updated_at은 직접 안 쳐도 자동으로 장착됩니다!

    @Id // 3. 기본키(PK) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. DB 자동 증가 번호(Auto-Increment)
    private Long id;

    @Column(name="kakao_id",  nullable=false, length=100, unique = true) // 중복 방지
    private String kakaoId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING) // 5. 문자열("BUYER") 그대로 DB에 저장! (중요)
    @Column(name = "role", nullable = false, length = 20)
    private MemberRole role;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status;

    // 6. [중요] JPA 스펙상 빈 껍데기 기본 생성자가 꼭 필요합니다! (protected로 안전하게 보호)
    protected Member() {
    }

    // 7. 객체를 생성할 때 쓸 생성자
    public Member(String kakaoId, String name, String phoneNumber) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = MemberRole.BUYER;      // 기본값은 구매자
        this.status = MemberStatus.ACTIVE;  // 기본값은 정상
    }

    // 8. 필요한 Getter들만 생성 (값을 마음대로 바꾸지 못하게 Setter는 만들지 않습니다!)
    public Long getId() { return id; }
    public String getKakaoId() { return kakaoId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public MemberRole getRole() { return role; }
    public MemberStatus getStatus() { return status; }

    //  9. 비즈니스 로직: 판매자 승격
    public void promoteToSeller() {
        if (this.role == MemberRole.BUYER) {
            this.role = MemberRole.SELLER;
        }
    }
}

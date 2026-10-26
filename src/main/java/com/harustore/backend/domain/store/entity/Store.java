package com.harustore.backend.domain.store.entity;

import com.harustore.backend.common.entity.BaseEntity;
import com.harustore.backend.domain.member.entity.Member;
import jakarta.persistence.*;

@Entity
@Table(name="store")
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 상점(Many) 입장에서 사장님(One)을 보며, 사장님 정보는 필요할 때만 가져온다.
    @JoinColumn(name = "seller_id", nullable = false) // DB store 테이블의 'seller_id' 컬럼에 사장님 회원(Member)의 id를 저장한다.
    private Member seller;

    @Column(name="name", nullable=false, length=100)
    private String name;

    @Column(name="address", nullable=false, length=255)
    private String address;

    @Column(name="phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name="operating_hours", nullable = false, columnDefinition = "Text")
    private String operatingHours;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false, length = 20)
    private StoreStatus  status;

    protected Store() {}

    public Store(Member seller ,String name, String address, String phoneNumber, String operatingHours, String description) {
        this.seller = seller;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
        this.description = description;
        this.status = StoreStatus.ACTIVE;

    }

    // 상점 정보 부분 수정(PATCH) 비즈니스 메서드
    public void patchInfo(String name, String address, String phoneNumber, String operatingHours, String description) {
        /*
         * 💡 [trim().isEmpty() vs isBlank() 차이점 정리]
         * 1) trim().isEmpty() (옛날 방식)
         *    - 공백을 잘라낸 새로운 String 객체를 힙 메모리에 하나 더 생성한 뒤 길이가 0인지 검사합니다.
         *    - 매번 새 문자열을 만들기 때문에 불필요한 메모리 낭비가 발생합니다.
         * 2) isBlank() (Java 11+ 최신 표준 방식)
         *    - 새로운 문자열 객체를 생성하지 않고, 문자열 내부를 직접 순회하며 빈칸("")이거나 스페이스바 공백("   ")만 있는지 바로 검사합니다.
         *    - 메모리 낭비가 전혀 없고 실행 속도도 더 빠르고 안전합니다!
         */
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (address != null && !address.isBlank()) {
            this.address = address;
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.phoneNumber = phoneNumber;
        }
        if (operatingHours != null && !operatingHours.isBlank()) {
            this.operatingHours = operatingHours;
        }
        // 소개글은 사장님이 내용을 지워서 빈칸으로 둘 수도 있으므로 null이 아닐 때 반영
        if (description != null) {
            this.description = description;
        }
    }

    public Long getId() {return id;}
    public Member getSeller() { return seller; }
    public String getName() {return name;}
    public String getAddress() {return address;}
    public String getPhoneNumber() {return phoneNumber;}
    public String getOperatingHours() {return operatingHours;}
    public String getDescription() {return description;}
    public StoreStatus getStatus() {return status;}

}

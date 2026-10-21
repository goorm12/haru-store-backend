package com.harustore.backend.common.entity;

// JPA 테이블 컬럼 매핑 및 속성 설정을 위한 어노테이션 임포트
import jakarta.persistence.Column;
// 엔티티의 생명주기 이벤트(저장, 수정 등)를 감지하는 리스너 지정 어노테이션 임포트
import jakarta.persistence.EntityListeners;
// 부모 클래스의 매핑 속성만 자식 엔티티에게 상속해주기 위한 어노테이션 임포트
import jakarta.persistence.MappedSuperclass;
// 엔티티가 최초 생성(INSERT)되어 저장될 때의 시간을 자동으로 주입하는 어노테이션 임포트
import org.springframework.data.annotation.CreatedDate;
// 엔티티가 수정(UPDATE)될 때의 시간을 자동으로 갱신하는 어노테이션 임포트
import org.springframework.data.annotation.LastModifiedDate;
// 스프링 Data JPA에서 제공하는 Auditing 감시자(이벤트 수신 리스너) 클래스 임포트
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// 날짜와 시간 정보를 다루기 위한 자바 8 최신 표준 날짜 클래스 임포트
import java.time.LocalDateTime;

/**
 * 모든 도메인 엔티티(Member, Store, Reservation 등)의 공통 매핑 정보를 담는 최상위 부모 클래스
 * 데이터가 생성된 일시(createdAt)와 최종 수정된 일시(updatedAt)를 개발자가 손으로 넣지 않고,
 * JPA Auditing 기능을 통해 자동으로 안전하게 기록·관리합니다.
 */
@MappedSuperclass // 실제 DB 테이블로 생성되지 않고, 자식 엔티티들에게 매핑 정보(컬럼들)만 물려주는 부모 클래스임을 선언
@EntityListeners(AuditingEntityListener.class) // 엔티티의 저장 및 수정 이벤트를 감지하여 자동으로 날짜를 채워 넣도록 리스너 연결
public abstract class BaseEntity { // 실수로 new BaseEntity() 객체를 직접 생성하지 못하도록 추상(abstract) 클래스로 선언

    @CreatedDate // 데이터가 최초 INSERT 될 때 현재 시간을 자동으로 주입
    @Column(name = "created_at", nullable = false, updatable = false) 
    // name = "created_at": DB 컬럼명을 스네이크 표기법으로 매핑
    // nullable = false: 생성일시가 null이 되지 않도록 NOT NULL 제약조건 부여
    // updatable = false: 데이터가 UPDATE 될 때 이 컬럼은 절대 수정되지 않도록 영구 보존
    private LocalDateTime createdAt;

    @LastModifiedDate // 데이터가 UPDATE 될 때마다 현재 시간으로 자동 갱신
    @Column(name = "updated_at", nullable = false)
    // name = "updated_at": DB 컬럼명을 스네이크 표기법으로 매핑
    // nullable = false: 수정일시가 null이 되지 않도록 NOT NULL 제약조건 부여
    private LocalDateTime updatedAt;

    /**
     * 데이터의 최초 생성일시를 조회한다.
     * 시간 정보는 JPA가 자동으로 주입하므로 외부에서 수정할 수 없도록 setter는 만들지 않는다.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 데이터의 최종 수정일시를 조회한다.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

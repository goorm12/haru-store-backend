package com.harustore.backend.config;

// 스프링 프레임워크의 설정(Configuration) 클래스임을 명시하는 어노테이션 임포트
import org.springframework.context.annotation.Configuration;
// JPA Auditing(생성일시, 수정일시 자동 감시 기능) 활성화 어노테이션 임포트
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 전역 설정 클래스
 * 스프링 부트 메인 클래스(@SpringBootApplication)에 @EnableJpaAuditing을 붙이지 않고,
 * 이렇게 별도의 @Configuration 클래스로 분리하면 향후 컨트롤러 슬라이스 테스트(@WebMvcTest) 시
 * JPA 관련 빈 누락 에러 없이 안전하게 테스트를 격리할 수 있습니다.
 */
@Configuration        // 스프링 컨테이너에 설정 파일(Bean 설정자)로 등록한다.
@EnableJpaAuditing    // BaseEntity의 @CreatedDate, @LastModifiedDate가 동작하도록 JPA 감시 기능을 활성화한다.
public class JpaConfig {
    // 추가적인 JPA 설정(예: AuditorAware로 등록자/수정자 ID 자동 주입 등)이 필요할 경우 여기에 Bean으로 등록 가능합니다.
}

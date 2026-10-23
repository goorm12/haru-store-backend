package com.harustore.backend.common.response;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.domain.Page;
import java.util.List;
/**
 * 프론트엔드가 쓰기 편하도록 꼭 필요한 정보만 추린 공통 페이징 응답 DTO
 */

@JsonPropertyOrder({ "content", "page", "totalElements", "totalPages", "hasNext", "last" })
public class PageResponse<T> {
    private final List<T> content;        // 실제 데이터 목록
    private final int page;               // 현재 페이지 번호 (0부터 시작)     // 페이지당 데이터 개수 (10)
    private final long totalElements;     // 전체 데이터 개수
    private final int totalPages;         // 전체 페이지 수
    private final boolean hasNext;        // 다음 페이지가 존재하는지 여부 (무한스크롤 핵심 ✨)
    private final boolean isLast;         // 마지막 페이지 여부

    // 스프링의 복잡한 Page 상자를 받아서 깔끔하게 재포장하는 생성자!
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.isLast = page.isLast();
    }

    // 편의 정적 팩토리 메서드
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page);
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isHasNext() { return hasNext; }
    public boolean isLast() { return isLast; }
}
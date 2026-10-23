package com.harustore.backend.domain.store.entity;

import com.harustore.backend.common.entity.BaseEntity;
import com.harustore.backend.domain.member.entity.Member;
import jakarta.persistence.*;

@Entity
@Table(
        name = "favorite_store",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_favorite_store_member", columnNames = {"member_id", "store_id"}) // 👈 중복 찜 방지!
        }
)
public class FavoriteStore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    @Column(name="is_default",  nullable = false)
    private boolean isDefault;

    protected FavoriteStore() {}

    public FavoriteStore(Member member, Store store, boolean isDefault) {
        this.member = member;
        this.store = store;
        this.isDefault = isDefault;
    }

    // 기본 상점 여부 변경 비즈니스 메서드
    public void updateDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Store getStore() { return store; }
    public boolean isDefault() { return isDefault; }

}

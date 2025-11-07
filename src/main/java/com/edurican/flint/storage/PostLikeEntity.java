package com.edurican.flint.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter

@Table(name = "post_likes",
        indexes = { @Index(name = "idx_posts_likes_user_id", columnList = "user_id"),
                @Index(name = "idx_posts_likes_id", columnList = "post_id") })
public class PostLikeEntity extends BaseEntity{
    @Column(name = "post_id", nullable = false)
    private Long postId;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    public PostLikeEntity(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    protected PostLikeEntity() {}
}

package com.edurican.flint.core.domain;

import com.edurican.flint.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_likes",
        indexes = { @Index(name = "idx_posts_likes_user_id", columnList = "user_id"),
                @Index(name = "idx_posts_likes_id", columnList = "post_id") })
public class PostLike extends BaseEntity {
    @Column(name = "post_id", nullable = false)
    private Long postId;
    @Column(name = "user_id", nullable = false)
    private Long userId;


}

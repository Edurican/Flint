package com.edurican.flint.core.domain;

import com.edurican.flint.storage.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comment_likes",
        indexes = { @Index(name = "idx_comments_likes_user_id", columnList = "user_id"),
                @Index(name = "idx_comments_likes_id", columnList = "comment_id") })
public class CommentLike extends BaseEntity {
    @Column(name = "comment_id",  nullable = false )
    private Long commentId;

    @Column(name = "user_id",   nullable = false )
    private Long userId;

    public CommentLike(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }

    protected CommentLike() {}
}

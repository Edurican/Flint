package com.edurican.flint.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comment_likes")
public class CommentLikeEntity extends BaseEntity{
    @Column(name = "comment_id",  nullable = false )
    private Long commentId;

    @Column(name = "user_id",   nullable = false )
    private Long userId;

    public CommentLikeEntity(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }

    protected CommentLikeEntity() {}
}

package com.edurican.flint.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "CommentLikes")
public class CommentLikeEntity extends BaseEntity{
    @Column(name = "comment_id")
    private Long commentId;
    private Long userId;

    public CommentLikeEntity(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }

    protected CommentLikeEntity() {}
}

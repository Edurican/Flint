package com.edurican.flint.storage;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class CommentLikeEntity extends BaseSoftEntity{
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id")
    private UserEntity commentId;

    public CommentLikeEntity(UserEntity commentId) {
        this.commentId = commentId;
    }
}

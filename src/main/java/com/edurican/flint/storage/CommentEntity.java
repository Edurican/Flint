package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //어떤식 로딩
    @JoinColumn(name = "user_id")
    private UserEntity user;

/*
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private PostEntity post;
*/

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_comment_id")
    private CommentEntity parentComment;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount;

    public CommentEntity(
            UserEntity user
            , CommentEntity  parentComment
            , String content
            , Integer likeCount
            //, Long commentId
    ) {
        this.user = user;
        this.parentComment = parentComment;
        this.content = content;
        this.likeCount = likeCount;
        // this.commentId = commentId;
    }
}

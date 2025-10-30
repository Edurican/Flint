package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends BaseSoftEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //어떤식 로딩
    @JoinColumn(name = "user_id")
    private Long user;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Long post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_comment_id")
    private Long parentComment;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount;

    public CommentEntity(
            Long user
            , Long  parentComment
            , Long post
            , String content
            , Integer likeCount
    ) {
        this.user = user;
        this.post = post;
        this.parentComment = parentComment;
        this.content = content;
        this.likeCount = likeCount;
    }
}

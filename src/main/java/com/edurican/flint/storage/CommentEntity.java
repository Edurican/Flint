package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentEntity extends BaseSoftEntity {

    @Column(name = "user_id",  nullable = false)
    private Long userId;


    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount;
}

package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comments_post", columnList = "postId")
        })
public class CommentEntity extends BaseSoftEntity {

    @Column(name = "user_id",  nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "parent_id")
    private Long parentCommentId;

    @Column(name = "depth")
    private Integer depth;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    public CommentEntity(Long userId, Long postId, Long parentCommentId, Integer depth, String content) {
        this.userId = userId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.depth = depth;
        this.content = content;
        this.likeCount = 0;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void updateLikeCount(Integer likeCount) {this.likeCount = likeCount;}

    public void increaseLike() {
        this.likeCount++;
    }

    public void decreaseLike() {
        if (this.likeCount > 0) {
            this.likeCount--;}
    }
}


package com.edurican.flint.core.domain;
import com.edurican.flint.storage.BaseSoftEntity;
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

public class Comment extends BaseSoftEntity {

    @Column(name = "user_id",  nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "parent_id")
    private Long parentCommentId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "depth", nullable = false)
    private Integer depth = 0;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    public Comment(Long userId, Long postId, Long parentCommentId, String username, Integer depth, String content) {
        this.userId = userId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.username = username;
        this.depth = depth;
        this.content = content;
        this.likeCount = 0;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}

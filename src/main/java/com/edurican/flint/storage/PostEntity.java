package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts",
        indexes = { @Index(name = "idx_posts_user_id", columnList = "user_id"),
        @Index(name = "idx_posts_topic_id", columnList = "topic_id") })
public class PostEntity extends BaseSoftEntity{

    @Column(name = "content", length = 100, nullable = false)
    private String content;

    @Column(name = "user_id", nullable = false )
    private Long userId;

    @Column(name = "topic_id", nullable=false )
    private Long topicId;

    @Column(name = "comment_count", nullable=false)
    private Integer commentCount;

    @Column(name = "view_count", nullable=false)
    private Integer viewCount;

    @Column(name = "like_count", nullable=false)
    private Integer likeCount;

    @Column(name = "respark_count", nullable=false)
    private Integer resparkCount;

    public void createPost(Long userId, String content, Long topicId) {
        this.userId = userId;
        this.content = content;
        this.topicId = topicId;
    }

    public void modifyPost(String content, Long topicId) {
        this.content = content;
        this.topicId = topicId;
    }
}

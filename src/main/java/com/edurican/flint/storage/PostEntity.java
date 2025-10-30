package com.edurican.flint.storage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts",
        indexes = { @Index(name = "idx_posts_user_id", columnList = "user_id"),
        @Index(name = "idx_posts_topic_id", columnList = "topic_id") })
public class PostEntity extends BaseSoftEntity{

    @Column(name = "content", length = 100)
    private String content;

    @Column(name = "user_id" )
    private Long userId;

    @Column(name = "topic_id", nullable=true)
    private Long topicId;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "respark_count")
    private Integer resparkCount;




}

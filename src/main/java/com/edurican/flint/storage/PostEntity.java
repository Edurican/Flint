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
public class PostEntity extends BaseEntity{

    @Column(name = "content", length = 100)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "topic_id")
    private TopicEntity topic;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "respark_count")
    private Integer resparkCount;

    //status로 하는거라서 삭제해도 될듯????
    @Column(name = "is_delete" , nullable = false)
    private boolean isDelete;


}

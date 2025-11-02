package com.edurican.flint.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "post_likes")
public class PostLikeEntity extends BaseEntity{
    @Column(name = "post_id")
    private Long postId;
    private Long userId;

    public PostLikeEntity(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    protected PostLikeEntity() {}
}

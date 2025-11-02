package com.edurican.flint.core.domain;

import com.edurican.flint.storage.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;
    private String content;
    private Long userId;
    private Long topicId;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Integer resparkCount;
    private String status;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public static Post of(PostEntity postEntity) {
        Post post = new Post();
        post.id = postEntity.getId();
        post.content = postEntity.getContent();
        post.userId = postEntity.getUserId();
        post.topicId = postEntity.getTopicId();
        post.viewCount = postEntity.getViewCount();
        post.commentCount = postEntity.getCommentCount();
        post.likeCount = postEntity.getLikeCount();
        post.resparkCount = postEntity.getResparkCount();
        post.status = postEntity.getStatus().toString();
        post.updatedAt = postEntity.getUpdatedAt();
        post.createdAt = postEntity.getCreatedAt();
        return post;
    }

}


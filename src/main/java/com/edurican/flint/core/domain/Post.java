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
    private String userName;
    private Long topicId;
    private String topicName;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Integer resparkCount;
    private String status;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public static Post of(PostEntity postEntity) {
        return Post.builder()
                .id(postEntity.getId())
                .content(postEntity.getContent())
                .userId(postEntity.getUserId())
                .topicId(postEntity.getTopicId())
                .viewCount(postEntity.getViewCount())
                .commentCount(postEntity.getCommentCount())
                .likeCount(postEntity.getLikeCount())
                .resparkCount(postEntity.getResparkCount())
                .status(postEntity.getStatus().name())
                .updatedAt(postEntity.getUpdatedAt())
                .createdAt(postEntity.getCreatedAt())
                .build();
    }
    public static Post of(PostEntity postEntity,String userName, String topicName) {
        return Post.builder()
                .id(postEntity.getId())
                .content(postEntity.getContent())
                .userId(postEntity.getUserId())
                .userName(userName)
                .topicId(postEntity.getTopicId())
                .topicName(topicName)
                .viewCount(postEntity.getViewCount())
                .commentCount(postEntity.getCommentCount())
                .likeCount(postEntity.getLikeCount())
                .resparkCount(postEntity.getResparkCount())
                .status(postEntity.getStatus().name())
                .updatedAt(postEntity.getUpdatedAt())
                .createdAt(postEntity.getCreatedAt())
                .build();
    }

}


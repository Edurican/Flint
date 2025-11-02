package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.domain.Post;
import com.edurican.flint.storage.PostEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@ApiResponse
public class PostResponse {
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

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .userId(post.getUserId())
                .topicId(post.getTopicId())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .resparkCount(post.getResparkCount())
                .status(post.getStatus())
                .updatedAt(post.getUpdatedAt())
                .createdAt(post.getCreatedAt())
                .build();
    }
}

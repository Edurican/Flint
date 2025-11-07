package com.edurican.flint.core.api.controller.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentSearchResponse {
    private Long id;
    private Long userId;
    private Long postId;
    private Long parentCommentId;
    private String content;
    private Integer likeCount;
    private Long replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CommentSearchResponse> replies;
}

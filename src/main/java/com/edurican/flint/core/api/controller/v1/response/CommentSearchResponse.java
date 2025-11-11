package com.edurican.flint.core.api.controller.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CommentSearchResponse {
    private Long commentId;
    private Long postId;
    private Long parentCommentId;
    private Integer depth;
    private String username;
    private String content;
    private Integer likeCount;
    private Long replyCount;
    private LocalDateTime createdAt;
    }
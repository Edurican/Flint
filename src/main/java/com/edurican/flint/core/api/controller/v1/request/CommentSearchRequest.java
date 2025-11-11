package com.edurican.flint.core.api.controller.v1.request;

import lombok.Data;

@Data
public class CommentSearchRequest {
    private Long postId;
    private Long parentCommentId;
}

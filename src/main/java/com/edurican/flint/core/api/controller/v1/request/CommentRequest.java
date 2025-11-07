package com.edurican.flint.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    private Long postId;

    private Long parentId;

    @NotBlank(message = "댓글을 작성해주세요.")
    private String comment;
}

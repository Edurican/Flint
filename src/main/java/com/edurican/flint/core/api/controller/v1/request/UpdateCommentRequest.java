package com.edurican.flint.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequest {

    private Long parentId;

    private String content;

    @NotBlank(message = "댓글을 작성해주세요.")
    private Long commentId;
}

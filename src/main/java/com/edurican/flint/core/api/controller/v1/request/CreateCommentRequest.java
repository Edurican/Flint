package com.edurican.flint.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotNull
    private Long postId;

    private Long parentCommentId;

    @NotBlank
    private String content;
}

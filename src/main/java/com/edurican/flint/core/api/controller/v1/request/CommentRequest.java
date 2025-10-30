package com.edurican.flint.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class CommentRequest {

    @NonNull
    private String postId;

    private Long parentCommentId;

    @NotBlank
    private String comment;


}

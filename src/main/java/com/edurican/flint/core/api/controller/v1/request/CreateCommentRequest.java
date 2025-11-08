package com.edurican.flint.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateCommentRequest {

    private Long parentId;

  //  @PositiveOrZero
    private Integer depth;

    @NotBlank(message = "댓글을 작성해주세요.")
    private String content;
}

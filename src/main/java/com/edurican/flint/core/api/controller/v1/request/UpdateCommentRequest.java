package com.edurican.flint.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequest {
    @NotBlank
    private String content;
}

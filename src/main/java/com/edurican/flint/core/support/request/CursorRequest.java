package com.edurican.flint.core.support.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "커서 요청")
public record CursorRequest(
        @Parameter(description = "마지막으로 조회한 ID")
        @Min(value = 1, message = "lastFetchedId는 1 이상이어야 합니다.")
        Long lastFetchedId,

        @Parameter(description = "조회할 개수 (20 ~ 50)")
        @Min(value = 10) @Max(value = 50)
        Integer limit
) {
    public CursorRequest {
        if (limit == null) limit = 20;
    }
}

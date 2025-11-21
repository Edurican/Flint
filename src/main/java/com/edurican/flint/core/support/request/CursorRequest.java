package com.edurican.flint.core.support.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorRequest {

    @Min(value = 1, message = "lastFetchedId는 1 이상이어야 합니다.")
    private Long lastFetchedId;

    private String lastFetchedType;

    @Min(value = 10, message = "limit은 10 이상이어야 합니다.")
    @Max(value = 50, message = "limit은 50 이하여야 합니다.")
    private Integer limit = 20;
}

package com.edurican.flint.core.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffsetLimit {
    private int  offset;
    private int limit;

    public Pageable toPageable() {
        return PageRequest.of(offset / limit, limit);
    }
}

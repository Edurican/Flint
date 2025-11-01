package com.edurican.flint.core.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Getter
public class OffsetLimit {
    private int offset;
    private int limit;

    public OffsetLimit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Pageable toPageable() {
        return PageRequest.of(offset / limit, limit);
    }
}

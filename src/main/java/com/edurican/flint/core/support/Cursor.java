package com.edurican.flint.core.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class Cursor<T> {
    private List<T> contents;
    private Long lastFetchedId;
    private Boolean hasNext;
    private String nextType;
}

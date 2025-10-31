package com.edurican.flint.core.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cursor<T> {
    private List<T> contents;
    private Long lastFetchedId;
}

package com.edurican.flint.core.support.response;

import lombok.Builder;

import java.util.List;


public record CursorResponse<T>(List<T> contents, Long lastFetchedId, Boolean hasNext, String nextType) {
}

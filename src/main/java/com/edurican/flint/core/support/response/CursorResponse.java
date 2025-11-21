package com.edurican.flint.core.support.response;

import com.edurican.flint.core.support.Cursor;
import lombok.Builder;

import java.util.List;


public record CursorResponse<T>(List<T> contents, Long lastFetchedId, Boolean hasNext) {

}

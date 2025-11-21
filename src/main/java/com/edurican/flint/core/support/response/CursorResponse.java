package com.edurican.flint.core.support.response;

import com.edurican.flint.core.support.Cursor;
import lombok.Builder;

import java.util.List;


public record CursorResponse<T>(List<T> contents, Long lastFetchedId, Boolean hasNext) {

    public static CursorResponse of(Cursor cursor) {
        return new CursorResponse<>(
                cursor.getContents(),
                cursor.getLastFetchedId(),
                cursor.getHasNext()
        );
    }
}

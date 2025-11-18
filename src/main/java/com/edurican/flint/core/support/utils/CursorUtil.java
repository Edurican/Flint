package com.edurican.flint.core.support.utils;

import java.util.List;

public class CursorUtil {

    public static Long getCursor(Long lastFetchedId) {
        return (lastFetchedId == null || lastFetchedId == 0) ? Long.MAX_VALUE : lastFetchedId;
    }

    public static <T> T nextCursor(List<T> content) {
        return (content.isEmpty()) ? null : content.get(content.size() - 1);
    }

    public static Boolean hasNextCursor(List<?> content, Integer limit) {
        return content.size() == limit;
    }
}

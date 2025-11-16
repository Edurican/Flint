package com.edurican.flint.storage;

import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.support.Cursor;

public interface FollowRepositoryCustom {
    Cursor<Follow> findFollowingByUsername(String username, Long lastFetchedId, Integer limit);
    Cursor<Follow> findFollowersByUsername(String username, Long lastFetchedId, Integer limit);

    Cursor<Follow> findFollowersNotFollowBack(Long userId, String keyword, Long lastFetchedId, Integer limit);
    Cursor<Follow> findSecondDegreeConnections(Long userId, String keyword, Long lastFetchedId, Integer limit);
    Cursor<Follow> findRecommendedFollowers(Long userId, String keyword, Long lastFetchedId, Integer limit);
}

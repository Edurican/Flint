package com.edurican.flint.storage;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.support.Cursor;

public interface FollowRepositoryCustom {
    Cursor<FollowResponse> findFollowingsByUserId(Long userId, Long lastFetchedId, Integer limit);
    Cursor<FollowResponse> findFollowersByUserId(Long userId, Long lastFetchedId, Integer limit);
    Cursor<FollowResponse> searchUsers(Long userId, String username, Long lastFetchedId, Integer limit);
}

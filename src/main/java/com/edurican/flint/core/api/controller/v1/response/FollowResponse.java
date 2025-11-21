package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.Follow;

import java.util.List;

public record FollowResponse(
    Long userId,
    String username,
    String name,
    String bio,
    Integer followersCount,
    Boolean isFollow
) {

}

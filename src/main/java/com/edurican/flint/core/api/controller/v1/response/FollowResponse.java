package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.Follow;

import java.util.List;

public record FollowResponse(
    Long id,
    Long followId,
    String username,
    String bio,
    Integer followersCount,
    Boolean isFollow
) {

}

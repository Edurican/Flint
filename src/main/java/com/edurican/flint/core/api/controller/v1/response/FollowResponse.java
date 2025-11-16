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

    public static FollowResponse of(Follow follow) {
        return new FollowResponse(
                follow.userId(),
                follow.name(),
                follow.username(),
                follow.bio(),
                follow.followersCount(),
                follow.isFollow()
        );
    }

    public static List<FollowResponse> of(List<Follow> follows) {
        return follows.stream()
                .map(FollowResponse::of)
                .toList();
    }
}

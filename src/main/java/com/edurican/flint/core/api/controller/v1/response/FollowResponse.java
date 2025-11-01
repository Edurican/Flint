package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.Follow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {
    private Long id;
    private Long followId;
    private String username;
    private LocalDateTime followedAt;

    public static FollowResponse of(Follow follow) {
        return new FollowResponse(follow.getId(), follow.getFollowId(), follow.getUsername(), follow.getFollowedAt());
    }

    public static List<FollowResponse> of(List<Follow> follows) {
        return follows.stream().map(FollowResponse::of).toList();
    }
}

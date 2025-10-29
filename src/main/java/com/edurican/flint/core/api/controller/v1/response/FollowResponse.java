package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.Follow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {
    private String username;

    public FollowResponse from(Follow follow) {
        return FollowResponse.builder().build();
    }
}

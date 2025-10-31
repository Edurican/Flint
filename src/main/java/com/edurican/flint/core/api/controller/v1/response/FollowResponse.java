package com.edurican.flint.core.api.controller.v1.response;

import com.edurican.flint.core.domain.Follow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {
    private Long followId;
    private String username;
    private LocalDateTime createdAt;
}

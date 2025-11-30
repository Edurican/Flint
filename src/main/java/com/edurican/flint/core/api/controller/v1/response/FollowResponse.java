package com.edurican.flint.core.api.controller.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팔로우 유저 조회")
public record FollowResponse(
        @Schema(description = "팔로우 테이블 PK", example = "1")
        Long id,

        @Schema(description = "대상 유저 ID", example = "42")
        Long followId,

        @Schema(description = "유저명", example = "john_doe")
        String username,

        @Schema(description = "자기소개", example = "자기소개")
        String bio,

        @Schema(description = "팔로워 수", example = "150")
        Integer followersCount,

        @Schema(description = "현재 대상 유저의 팔로우 여부", example = "true")
        Boolean isFollow
) {

}

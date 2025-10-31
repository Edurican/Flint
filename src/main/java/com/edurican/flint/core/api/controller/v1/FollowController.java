package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.response.ExampleResponseDto;
import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.domain.FollowService;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/api/v1/{userId}/followers")
    @Operation(summary = "팔로워 불러오기", description = "특정 유저 팔로워 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FollowResponse.class)))})
    })
    public ApiResult<List<FollowResponse>> getFollowers(@PathVariable Long userId) {
        List<Follow> follows = followService.getFollowers(userId);
        List<FollowResponse> followers = follows.stream()
                .map(follow ->
                        FollowResponse.builder()
                                .followId(follow.getFollowId())
                                .username(follow.getUsername())
                                .createdAt(follow.getCreatedAt())
                                .build()
                )
                .toList();

        return ApiResult.success(followers);
    }

    @GetMapping("/api/v1/{userId}/following")
    @Operation(summary = "팔로잉 불러오기", description = "특정 유저 팔로잉 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FollowResponse.class)))})
    })
    public ApiResult<List<FollowResponse>> getFollowing(@PathVariable Long userId) {
        List<Follow> follows = followService.getFollowing(userId);
        List<FollowResponse> following = follows.stream()
                .map(follow ->
                        FollowResponse.builder()
                                .followId(follow.getFollowId())
                                .username(follow.getUsername())
                                .createdAt(follow.getCreatedAt())
                                .build()
                )
                .toList();

        return ApiResult.success(following);
    }

    @PostMapping("/api/v1/{followId}/follow")
    @Operation(summary = "팔로워", description = "유저 팔로워하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
    })
    public ApiResult<Boolean> follow(@PathVariable Long followId) {
        long userId = 1;
        followService.follow(userId, followId);
        return ApiResult.success(true);
    }

    @DeleteMapping("/api/v1/{unfollowId}/follow")
    @Operation(summary = "언팔로워", description = "유저 언팔로워하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
    })
    public ApiResult<Boolean> unfollow(@PathVariable Long unfollowId) {
        long userId = 1;
        followService.unfollow(userId, unfollowId);
        return ApiResult.success(true);
    }


}

package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.api.controller.v1.response.UserResponse;
import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.domain.FollowService;
import com.edurican.flint.core.domain.User;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.core.support.response.ApiResult;
import com.edurican.flint.core.support.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResult<CursorResponse<FollowResponse>> getFollowers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId,
            @RequestParam(name = "lastFetchedId", required = false) Long lastFetchedId,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit
    ) {
        Cursor<Follow> follows = followService.getFollowers(userId, lastFetchedId, limit);
        List<FollowResponse> followers = follows.getContents().stream().map(FollowResponse::of).toList();
        return ApiResult.success(new CursorResponse(followers, follows.getLastFetchedId(), follows.getHasNext()));
    }

    @GetMapping("/api/v1/{userId}/following")
    @Operation(summary = "팔로잉 불러오기", description = "특정 유저 팔로잉 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FollowResponse.class)))})
    })
    public ApiResult<CursorResponse<FollowResponse>> getFollowing(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId,
            @RequestParam(name = "lastFetchedId", required = false) Long lastFetchedId,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit
    ) {
        Cursor<Follow> follows = followService.getFollowing(userId, lastFetchedId, limit);
        List<FollowResponse> following = follows.getContents().stream().map(FollowResponse::of).toList();
        return ApiResult.success(new CursorResponse(following, follows.getLastFetchedId(), follows.getHasNext()));
    }

    @GetMapping("/api/v1/search")
    @Operation(summary = "팔로우 검색", description = "팔로우 검색 피드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FollowResponse.class)))})
    })
    public ApiResult<CursorResponse<UserResponse>> searchFollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "lastFetchedId", required = false) Long lastFetchedId,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit
    ) {
        Cursor<User> users = followService.searchFollow(userDetails.getUser(), username, lastFetchedId, limit);
        List<UserResponse> userResponses = users.getContents().stream().map(UserResponse::of).toList();
        return ApiResult.success(new CursorResponse(userResponses, users.getLastFetchedId(), users.getHasNext()));
    }

    @PostMapping("/api/v1/{followId}/follow")
    @Operation(summary = "팔로워", description = "유저 팔로워하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
    })
    public ApiResult<Boolean> follow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long followId
    ) {
        followService.follow(userDetails.getUser().getId(), followId);
        return ApiResult.success(true);
    }

    @DeleteMapping("/api/v1/{unfollowId}/follow")
    @Operation(summary = "언팔로워", description = "유저 언팔로워하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
    })
    public ApiResult<Boolean> unfollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long unfollowId
    ) {
        followService.unfollow(userDetails.getUser().getId(), unfollowId);
        return ApiResult.success(true);
    }
}

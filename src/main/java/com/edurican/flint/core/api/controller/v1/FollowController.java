package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.response.FollowResponse;
import com.edurican.flint.core.support.request.CursorRequest;
import com.edurican.flint.core.domain.Follow;
import com.edurican.flint.core.domain.FollowService;
import com.edurican.flint.core.domain.User;
import com.edurican.flint.core.support.Cursor;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.core.support.response.ApiResult;
import com.edurican.flint.core.support.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/api/v1/{username}/followers")
    @Operation(summary = "팔로워 불러오기", description = "특정 유저 팔로워 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    public ApiResult<CursorResponse<FollowResponse>> getFollowers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String username,
            @Valid @ModelAttribute CursorRequest cursor
    ) {
        return ApiResult.success(followService.getFollowers(username, cursor));
    }

    @GetMapping("api/v1/{username}/following")
    @Operation(summary = "팔로잉 불러오기", description = "특정 유저 팔로잉 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    public ApiResult<CursorResponse<FollowResponse>> getFollowing(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String username,
            @Valid @ModelAttribute CursorRequest cursor
    ) {
        return ApiResult.success(followService.getFollowing(username, cursor));
    }

    @GetMapping("/api/v1/search")
    @Operation(summary = "팔로우 검색", description = "팔로우 검색 피드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    public ApiResult<CursorResponse<FollowResponse>> searchFollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "username", required = false) String username,
            @Valid @ModelAttribute CursorRequest cursor
    ) {
        return ApiResult.success(followService.searchFollow(userDetails.getUser(), username, cursor));
    }

    @PostMapping("/api/v1/{userId}/follow")
    @Operation(summary = "유저 팔로우", description = "유저 팔로우 및 맞팔로우")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로우 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    public ApiResult<?> follow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId
    ) {
        followService.follow(userDetails.getUser().getId(), userId);
        return ApiResult.success();
    }

    @DeleteMapping("/api/v1/{userId}/follow")
    @Operation(summary = "유저 언팔로우", description = "팔로우한 유저 언팔로우")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "언팔로우 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
    })
    public ApiResult<?> unfollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId
    ) {
        followService.unfollow(userDetails.getUser().getId(), userId);
        return ApiResult.success();
    }
}

package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.request.*;
import com.edurican.flint.core.api.controller.v1.response.LoginResponseDto;
import com.edurican.flint.core.api.controller.v1.response.UserProfileResponse;
import com.edurican.flint.core.domain.ImageFileService;
import com.edurican.flint.core.domain.UserService;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.core.support.response.ApiResult;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
//@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;
    private final ImageFileService  imageFileService;

    public UserController(UserService userService, ImageFileService imageFileService) {
        this.userService = userService;
        this.imageFileService = imageFileService;
    }

    @PostMapping("/api/v1/auth/signup")
    @Operation(summary = "회원 가입", description = "회원 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<String> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {

            userService.signUp(signupRequestDto);
            return ApiResult.success("회원가입에 성공했습니다.");
    }

    @PostMapping("/api/v1/auth/login")
    @Operation(summary = "로그인", description = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);

        return ApiResult.success(loginResponseDto);
        }

    @GetMapping("/api/v1/users/{username}")
    @Operation(summary = "유저 프로필 조회", description = "username으로 유저 프로필 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<UserProfileResponse> getUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String username) {
        return ApiResult.success(userService.getUserProfileByUsername(username, userDetails.getUser().getId()));
    }

    @PutMapping("/api/v1/users/me")
    @Operation(summary = "내 프로필 수정", description = "username, bio를 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<String> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ProfileUpdateRequestDto profileUpdateRequestDto)  {

        String newToken = userService.updateProfile(userDetails, profileUpdateRequestDto);
        return ApiResult.success(newToken);
    }

    @GetMapping("/api/v1/users/me")
    @Operation(summary = "내 프로필 조회", description = "로그인된 사용자의 프로필 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserProfileResponse myProfile = userService.getUserProfileByUsername(
                userDetails.getUsername(),
                userDetails.getUser().getId());

        return ApiResult.success(myProfile);
    }


    @PatchMapping("/api/v1/users/me/image")
    @Operation(summary = "내 프로필 사진 경로 수정", description = "presigned-url 경로 수정 테스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<String> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ProfileImageUpdateRequestDto profileImageUpdateRequestDto)  {

        String newProfileImageUrl = userService.updateProfileImage(userDetails, profileImageUpdateRequestDto.getImagePath());
        return ApiResult.success(newProfileImageUrl);
    }

    @DeleteMapping("/api/v1/users/me/image")
    @Operation(summary = "내 프로필 사진 경로 삭제", description = "내 프로필 사진 기본으로 변경 테스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<String> deleteMyProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteProfileImage(userDetails);

        return ApiResult.success("기본 이미지로 변경되었습니다.");
    }
}


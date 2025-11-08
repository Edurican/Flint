package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.request.LoginRequestDto;
import com.edurican.flint.core.api.controller.v1.request.ProfileUpdateRequestDto;
import com.edurican.flint.core.api.controller.v1.request.SignupRequestDto;
import com.edurican.flint.core.api.controller.v1.response.LoginResponseDto;
import com.edurican.flint.core.api.controller.v1.response.UserProfileResponse;
import com.edurican.flint.core.domain.UserService;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

        //            String token = userService.login(loginRequestDto);
        //            return ApiResult.success(token);
        }

    @GetMapping("/api/v1/users/{username}")
    @Operation(summary = "유저 프로필 조회", description = "username으로 유저 프로필 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<UserProfileResponse> getUserProfile(@PathVariable String username) {
        UserProfileResponse userProfile = userService.getUserProfileByUsername(username);
        return ApiResult.success(userProfile);
    }

    @PutMapping("/api/v1/users/me")
    @Operation(summary = "내 프로필 수정", description = "username, bio를 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<String> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ProfileUpdateRequestDto profileUpdateRequestDto)  {
        userService.updateProfile(userDetails, profileUpdateRequestDto);
        return ApiResult.success("프로필이 성공적으로 수정되었습니다.");
    }
}


package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.request.LoginRequestDto;
import com.edurican.flint.core.api.controller.v1.request.SignupRequestDto;
import com.edurican.flint.core.api.controller.v1.response.ExampleResponseDto;
import com.edurican.flint.core.domain.UserService;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExampleResponseDto.class))})
    })
    public ApiResult<String> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        try {
            userService.signUp(signupRequestDto);
            return ApiResult.success("회원가입에 성공했습니다.");
        } catch (Exception e) {
            return ApiResult.error(ErrorType.DEFAULT_ERROR);
        }
    }

    /* UserService에서 Jwt 발급 로직 필요!!! */
    @PostMapping("/api/v1/auth/login")
    @Operation(summary = "팔로잉 불러오기", description = "특정 유저 팔로잉 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExampleResponseDto.class))})
    })
    public ApiResult<String> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            String token = userService.login(loginRequestDto);
            return ApiResult.success("로그인에 성공했습니다.");
        } catch (Exception e) {
            return ApiResult.error(ErrorType.DEFAULT_ERROR);
        }
    }
}

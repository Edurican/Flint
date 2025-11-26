package com.edurican.flint.core.domain;

import com.edurican.flint.core.api.controller.v1.request.LoginRequestDto;
import com.edurican.flint.core.api.controller.v1.request.SignupRequestDto;
import com.edurican.flint.core.api.controller.v1.response.LoginResponseDto;
import com.edurican.flint.core.enums.UserRoleEnum;
import com.edurican.flint.core.support.auth.JwtUtil;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 서비스 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("회원가입 서비스 테스트")
    class SignUp {

        @Test
        @DisplayName("성공 - 정상적인 회원가입")
        void success() {
            // given
            SignupRequestDto request = new SignupRequestDto();
            request.setEmail("iamsuccess@success.com");
            request.setPassword("password");
            request.setUsername("iamsuccess");

            given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
            given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.empty());
            given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

            // when
            userService.signUp(request);

            // then
            verify(userRepository).save(any());
        }

        @Test
        @DisplayName("실패 - 중복된 이메일")
        void failDuplicatedEmail() {
            // given
            SignupRequestDto request = new SignupRequestDto();
            request.setEmail("duplicated@email");
            request.setPassword("password");
            request.setUsername("duplicatedUsername");

            given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(new User()));

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                userService.signUp(request);
            });

            // then
            Assertions.assertThat(exception.getErrorType()).isEqualTo(ErrorType.USER_DUPLICATE_EMAIL);
        }

        @Test
        @DisplayName("실패 - 중복된 유저이름")
        void failDuplicateUsername() {
            SignupRequestDto request = new SignupRequestDto();
            request.setEmail("duplicated@email");
            request.setPassword("password");
            request.setUsername("duplicatedUsername");

            given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.of(new User()));

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                userService.signUp(request);
            });

            // then
            Assertions.assertThat(exception.getErrorType()).isEqualTo(ErrorType.USER_DUPLICATE_USERNAME);

        }
    }

    @Nested
    @DisplayName("로그인 서비스 테스트")
    class SignIn {

        @Test
        @DisplayName("성공 - 정상적인 로그인")
        void successLogin() {

            // given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("success@success");
            loginRequest.setPassword("password");

            User fakeUser = new User(
                    "username",
                    "encoded_password",
                    "success@success",
                    UserRoleEnum.USER
            );


            given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(fakeUser));
            given(passwordEncoder.matches(loginRequest.getPassword(), fakeUser.getPassword())).willReturn(true);
            given(jwtUtil.createJwtToken(fakeUser.getUsername())).willReturn("fake-token");

            // when
            LoginResponseDto response = userService.login(loginRequest);

            // then
            Assertions.assertThat(response.getUsername()).isEqualTo(fakeUser.getUsername());
            Assertions.assertThat(response.getToken()).isEqualTo("fake-token");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 이메일")
        void failNotFoundEmail() {

            // given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("notfound@email");
            loginRequest.setPassword("password");
            given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                userService.login(loginRequest);
            });

            // then
            Assertions.assertThat(exception.getErrorType()).isEqualTo(ErrorType.USER_NOT_FOUND_BY_EMAIL);
        }

        @Test
        @DisplayName("실패 - 일치하지 않는 비밀번호")
        void failMissMatchPassword() {

            // given
            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail("notfound@email");
            loginRequest.setPassword("wrongpassword");

            User fakeUser = new User(
                    "notfound@email",
                    "password",
                    "fakeuser",
                    UserRoleEnum.USER
            );

            given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(fakeUser));
            given(passwordEncoder.matches(loginRequest.getPassword(), fakeUser.getPassword())).willReturn(false);

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                userService.login(loginRequest);
            });

            // then
            Assertions.assertThat(exception.getErrorType()).isEqualTo(ErrorType.USER_PASSWORD_MISMATCH);
        }
    }
}
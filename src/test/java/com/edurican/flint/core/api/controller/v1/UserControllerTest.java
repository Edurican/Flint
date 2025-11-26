package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.request.LoginRequestDto;
import com.edurican.flint.core.api.controller.v1.request.SignupRequestDto;
import com.edurican.flint.core.api.controller.v1.response.LoginResponseDto;
import com.edurican.flint.core.domain.UserService;
import com.edurican.flint.core.support.auth.JwtAuthenticationFilter;
import com.edurican.flint.core.support.auth.JwtUtil;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.UserRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("유저 컨트롤러 테스트")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ExceptionController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Gson gson;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private JobRepository jobRepository;

    @MockitoBean
    private JobExplorer jobExplorer;

    @MockitoBean
    private BatchProperties batchProperties;

    @Nested
    @DisplayName("회원가입 컨트롤러 테스트")
    class SignUp {

        @Test
        @DisplayName("성공 - 회원가입")
        void successSignUp() throws Exception {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setEmail("success@success");
            signupRequestDto.setPassword("password");
            signupRequestDto.setUsername("success@success");
            String content = gson.toJson(signupRequestDto);

            willDoNothing().given(userService).signUp(any());

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content)
                                    .with(csrf())
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

        }

        @Test
        @DisplayName("실패 - 중복된 이메일")
        void failDuplicatedEmailSignUp() throws Exception {

            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setEmail("failure@sfailure");
            signupRequestDto.setPassword("password");
            signupRequestDto.setUsername("failure");
            String content = gson.toJson(signupRequestDto);

            // when & then
            willThrow(new CoreException(ErrorType.USER_DUPLICATE_EMAIL)).given(userService).signUp(any());
            mockMvc.perform(
                            post("/api/v1/auth/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content)
                                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("실패 - 중복된 이름")
        void failDuplicatedUsernameSignUp() throws Exception {

            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setEmail("failure@sfailure");
            signupRequestDto.setPassword("password");
            signupRequestDto.setUsername("failure");
            String content = gson.toJson(signupRequestDto);

            // when & then
            willThrow(new CoreException(ErrorType.USER_DUPLICATE_USERNAME)).given(userService).signUp(any());
            mockMvc.perform(
                            post("/api/v1/auth/signup")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content)
                                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("로그인 컨트롤러 테스트")
    class Login {

        @Test
        @DisplayName("성공 - 로그인")
        void successLogin() throws Exception {
            // given
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setEmail("success@success");
            loginRequestDto.setPassword("password");

            String content = gson.toJson(loginRequestDto);

            LoginResponseDto fakeResponse = new LoginResponseDto("fake-token", "fakeuser");

            given(userService.login(any(LoginRequestDto.class)))
                    .willReturn(fakeResponse);

            // when & then
            mockMvc.perform(
                    post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 이메일")
        void failNotFoundEmailLogin() throws Exception {
            // given
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setEmail("failure@sfailure");
            loginRequestDto.setPassword("password");
            String content = gson.toJson(loginRequestDto);

            given(userService.login(any(LoginRequestDto.class))).willThrow(new CoreException((ErrorType.USER_NOT_FOUND_BY_EMAIL)));

            // when & then
            mockMvc.perform(
                    post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("실패 - 일치하지 않는 비밀번호")
        void failMissMatchPassword()  throws Exception {
            // given
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setEmail("failure@sfailure");
            loginRequestDto.setPassword("password");
            String content = gson.toJson(loginRequestDto);

            given(userService.login(any(LoginRequestDto.class))).willThrow(new CoreException(
                    ErrorType.USER_PASSWORD_MISMATCH));

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content)
                                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }
}
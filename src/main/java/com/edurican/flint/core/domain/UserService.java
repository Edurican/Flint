package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.LoginRequestDto;
import com.edurican.flint.core.api.controller.v1.request.SignupRequestDto;
import com.edurican.flint.core.api.controller.v1.response.LoginResponseDto;
import com.edurican.flint.core.enums.UserRoleEnum;
import com.edurican.flint.core.support.auth.JwtUtil;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.storage.UserEntity;
import com.edurican.flint.storage.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /* 회원가입 서비스 */
    public void signUp(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String email = signupRequestDto.getEmail();

        UserRoleEnum role = UserRoleEnum.USER;

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new CoreException(ErrorType.USER_DUPLICATE_USERNAME);
                });

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new CoreException(ErrorType.USER_DUPLICATE_EMAIL);
                });

        UserEntity user = new UserEntity(username, password, email, role);
        userRepository.save(user);
    }

    /* 로그인 서비스 */
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new CoreException(ErrorType.USER_NOT_FOUND_BY_EMAIL)
        );

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new CoreException(ErrorType.USER_PASSWORD_MISMATCH);
        }

        String token = jwtUtil.createJwtToken(user.getUsername());
        System.out.println("🔹 로그인 시도: " + email);
        System.out.println("🔹 DB 비밀번호: " + user.getPassword());
        System.out.println("🔹 입력 비밀번호 일치? " + passwordEncoder.matches(password, user.getPassword()));

        return new LoginResponseDto(token, user.getUsername());

    }
}

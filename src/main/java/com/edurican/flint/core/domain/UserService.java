package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.LoginRequestDto;
import com.edurican.flint.core.api.controller.v1.request.SignupRequestDto;
import com.edurican.flint.core.enums.UserRoleEnum;
import com.edurican.flint.core.support.auth.JwtUtil;
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
                    throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
                });

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new IllegalArgumentException("등록된 이메일입니다.");
                });

        UserEntity user = new UserEntity(username, password, email, role);
        userRepository.save(user);
    }

    /* 로그인 서비스 */
    public String login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        UserEntity user = userRepository.findByUsername(email).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createJwtToken(user.getUsername());

        return token;

    }
}

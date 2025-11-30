package com.edurican.flint.core.domain;


import com.edurican.flint.core.api.controller.v1.request.LoginRequestDto;
import com.edurican.flint.core.api.controller.v1.request.ProfileUpdateRequestDto;
import com.edurican.flint.core.api.controller.v1.request.SignupRequestDto;
import com.edurican.flint.core.api.controller.v1.response.LoginResponseDto;
import com.edurican.flint.core.api.controller.v1.response.UserProfileResponse;
import com.edurican.flint.core.enums.UserRoleEnum;
import com.edurican.flint.core.support.auth.JwtUtil;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.storage.FollowRepository;
import com.edurican.flint.storage.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PostService postService;
    private final ImageFileService imageFileService;

    public UserService(UserRepository userRepository, FollowRepository followRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, PostService postService, ImageFileService imageFileService) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.postService = postService;
        this.imageFileService = imageFileService;
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

        User user = new User(username, password, email, role);
        userRepository.save(user);
    }

    /* 로그인 서비스 */
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CoreException(ErrorType.USER_NOT_FOUND_BY_EMAIL)
        );

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new CoreException(ErrorType.USER_PASSWORD_MISMATCH);
        }

        String token = jwtUtil.createJwtToken(user.getUsername());
        System.out.println("🔹 로그인 시도: " + email);
        System.out.println("🔹 DB 비밀번호: " + user.getPassword());
        System.out.println("🔹 입력 비밀번호 일치? " + passwordEncoder.matches(password, user.getPassword()));

        return new LoginResponseDto(token, user.getUsername(), user.getProfileImageUrl());

    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByUsername(String username, Long currentUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND));

        long postCount = postService.getPostCountByUserId(user.getId());

        boolean isFollowing = false;
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());
        }

        return new UserProfileResponse(user, postCount, isFollowing);
    }

    /*
    * 프로필 업데이트 메소드
    * */
    @Transactional
    public String updateProfile(UserDetailsImpl userDetails, ProfileUpdateRequestDto profileUpdateRequestDto) {
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CoreException(ErrorType.USER_NOT_FOUND));

        String newUsername = profileUpdateRequestDto.getUsername();
        String newBio = profileUpdateRequestDto.getBio();
        String newToken = null;

        // username 중복 감지 로직
        if(newUsername != null && !newUsername.equals(user.getUsername())) {
            userRepository.findByUsername(newUsername)
                    .ifPresent(existingUser -> {
                        throw new CoreException(ErrorType.USER_DUPLICATE_USERNAME);
                    });
            user.updateProfile(newUsername, newBio);
            newToken = jwtUtil.createJwtToken(newUsername);

        } else {
            user.updateProfile(null, newBio);
        }

//        user.updateProfile(newUsername, newBio);
        return newToken;
    }

    /* 프로필 사진 경로 변경 메소드 */
    @Transactional
    public String updateProfileImage(UserDetailsImpl userDetails, String filePath) {
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CoreException(ErrorType.USER_NOT_FOUND)
        );

        user.updateProfileImageUrl(filePath);

        return filePath;
    }

    /* 프로필 사진 경로 초기화 메소드 (기본 이미지로 변경) */
    @Transactional
    public void deleteProfileImage(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CoreException(ErrorType.USER_NOT_FOUND)
        );

        user.deleteProfileImageUrl();
    }
}

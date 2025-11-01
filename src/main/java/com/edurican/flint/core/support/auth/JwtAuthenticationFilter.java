package com.edurican.flint.core.support.auth;

import com.edurican.flint.core.domain.User;
import com.edurican.flint.core.support.request.UserDetailsImpl;
import com.edurican.flint.storage.UserEntity;
import com.edurican.flint.storage.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/* 모든 API 요청을 가로채 JWT를 검사하는 핵심 필터 */

    @Slf4j(topic = "JWT 검증 및 인가")
    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;
        private final UserRepository userRepository;

        public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
            this.jwtUtil = jwtUtil;
            this.userRepository = userRepository;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String tokenValue = request.getHeader("Authorization");

            // 인증 방식의 종류를 알려주는 'Bearer'를 토큰에 삽입(추출 할 때에는 파싱)
            if (tokenValue != null && tokenValue.startsWith("Bearer ")) {
                String token = tokenValue.substring(7);

                try {
                    Claims userInfo = jwtUtil.getUserFromJwtToken(token);
                    String username = userInfo.getSubject();

                    UserEntity user = userRepository.findByUsername(username).orElseThrow(
                            () -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                    // 11.01 세번째 인자 권한은 추후 수정 예정
                    UserDetails userDetails = new UserDetailsImpl(user);
//                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), null);

                    // 비밀번호는 이미 처리했으므로 null
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 컨텍스트 생성 후 인증 객체와 인증된 사용자로 등록 로직
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);

                } catch (Exception e) {
                    log.error("토큰 에러 : " + e.getMessage());
                }
            }

            filterChain.doFilter(request, response);
        }
    }


package com.edurican.flint.core.api.config;

import com.edurican.flint.core.support.auth.JwtAuthenticationFilter;
import com.edurican.flint.core.support.auth.JwtUtil;
import com.edurican.flint.storage.UserRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil  jwtUtil;
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter  jwtAuthenticationFilter;

    public SecurityConfig(JwtUtil jwtUtil, UserRepository userRepository,  JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 기본 패스워드 발급 방지 메소드, 호출 시 예외
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("이 UserDetailsService는 사용되지 않습니다.");
        };
    }

    /*
    * securityFilterChain으로 시큐리티 설정
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호 비활성화
        http.csrf(csrf -> csrf.disable());

        // 폼(Form) 기반 로그인 비활성화
        http.formLogin(form -> form.disable());

        // HTTP Basic 인증 방식 비활성화
        http.httpBasic(basic -> basic.disable());

        // JWT 발급으로 인한 세션 사용 불필요
        http.sessionManagement(sessionManagement
                -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 우선 모두에게 API 접근 허용(추후 수정)
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
//                .anyRequest().permitAll()
                .anyRequest().authenticated() // auth 제외하고 모든 요청을 인증 받아야 함. (개발 단계이므로 일단 모두 허용)
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

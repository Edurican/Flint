package com.edurican.flint.core.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    * securityFilterChain으로 시큐리티 설정
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호 비활성화
        http.csrf(csrf -> csrf.disable());

        // JWT 발급으로 인한 세션 사용 불필요
        http.sessionManagement(sessionManagement
                -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 우선 모두에게 API 접근 허용(추후 수정)
        http.authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
        );

        return http.build();
    }
}

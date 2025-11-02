package com.edurican.flint.core.api.config;

import com.edurican.flint.core.support.auth.JwtAuthenticationFilter;
import com.edurican.flint.core.support.auth.JwtUtil;
import com.edurican.flint.storage.UserRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Vue.js 개발 서버 주소(Vite 기본 5173, Vue CLI 기본 8081)
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8081"));

        // 허용할 HTTP 메서드 (GET, POST 등)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 HTTP 헤더 (Authorization 등)
        config.setAllowedHeaders(Arrays.asList("*"));

        // 자격 증명(토큰, 쿠키) 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 API 경로("/**")에 대해 위 설정을 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /*
    * securityFilterChain으로 시큐리티 설정
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS 설정 활성화
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

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
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-docs/json/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated() // auth 제외하고 모든 요청을 인증 받아야 함. (개발 단계이므로 일단 모두 허용)
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

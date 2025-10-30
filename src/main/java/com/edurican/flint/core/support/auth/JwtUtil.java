package com.edurican.flint.core.support.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/*
* JWT를 만들고 토큰이 유효한지 검증하는 유틸리티 클래스
* */
@Component
public class JwtUtil {

    // 서명에 사용할 키
    @Value("${jwt.secret.key}")
    private String secretKey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.ES256;

    // 키를 Base64로 디코딩 하여 Key 생성
    @jakarta.annotation.PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /*
    * JWT 생성 메소드
    * */
    public String createJwtToken(String username) {
        Date date = new Date();
        long TOKEN_TIME = 60 * 60 * 1L;

        return Jwts.builder()
                .setSubject(username) // 사용자 이름
                .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급 일자
                .signWith(key, signatureAlgorithm) // 서명
                .compact();
    }

    // JWT 검증 및 정보 추출
    public Claims getUserFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJwt(token).getBody();
    }
}

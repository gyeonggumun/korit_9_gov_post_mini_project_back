package com.korit.post_mini_project_back.jwt;

import com.korit.post_mini_project_back.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;

    // @value는 yml의 값을 들고와서 사용할 때 씀
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) { // jwt에 맞는 key라고 하는 객체생성
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰을 만들기 위해 user정보가 필요해서 매개변수로 UserEntity를 받아옴
    public String createAccessToken(User userEntity) {
        Date now = new Date();  // 현재시간
        Long expiredTime = now.getTime() + (1000l * 60l * 60l * 24l); // 현시점에서 24시간 증가
        Date expiredDate = new Date(expiredTime);   // Date로 타입 변환 new 되는 시점 부터 24시간


        return Jwts.builder()
                .subject("Server Access Token") // 제목
                .issuer("문경구") // 이름
                .issuedAt(new Date()) // 현시간 설정
                // 위 3개는 없어도 괜찮지만 아래 3개는 필수로 필요한 항목
                .expiration(expiredDate) // 만료시간
                .claim("userId", userEntity.getUserId())  // 몇번째유저인지
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(key)
                .build();
        jwtParser.parseClaimsJws(token);
        return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public int getUserId (String token) {
        return (int) Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getPayload()
                .get("userId");
    }
}
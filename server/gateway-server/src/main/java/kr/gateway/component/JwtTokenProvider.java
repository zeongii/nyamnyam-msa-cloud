package kr.gateway.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Getter
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expired.access}")
    private long accessTokenExpired;

    @Value("${jwt.expired.refresh}")
    private long refreshTokenExpired;

    public Mono<String> generateToken(UserDetails userDetails, boolean isRefresh) {
         Date now = new Date();
         Date expiration = new Date(now.getTime() + accessTokenExpired);

         // 비밀 키를 Key 객체로 변환
         byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
         Key key = Keys.hmacShaKeyFor(keyBytes);

         String token = Jwts.builder()
                 .setSubject(userDetails.getUsername())
                 .setIssuedAt(now)
                 .setExpiration(expiration)
                 .signWith(key) // 변경된 부분
                 .compact();

        return Mono.just(token);
    }

    public Mono<String> createOAuthToken(String username, String objectId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpired);

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .setSubject(username) // username을 subject로 설정
                .claim("objectId", objectId) // objectId를 클레임으로 추가
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        return Mono.just(token);
    }

    public Mono<Boolean> validateToken(String token) {
        try {
            Jwts.parser() // parserBuilder 사용
                    .verifyWith(secret) // 비밀 키 설정
                    .build() // JwtParser 생성
                    .parseSignedClaims(token); // 토큰 파싱
            return Mono.just(true);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 처리
            return Mono.just(false);
        } catch (SignatureException e) {
            // 서명이 잘못된 토큰 처리
            return Mono.just(false);
        } catch (MalformedJwtException e) {
            // 형식이 잘못된 토큰 처리
            return Mono.just(false);
        } catch (Exception e) {
            // 기타 예외 처리
            return Mono.just(false);
        }
    }


    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Mono<String> refreshToken(String oldToken) {
        String userId = getUserIdFromToken(oldToken);
        return createToken(userId);
    }

    public Mono<Void> invalidateToken(String token) {
        return Mono.empty();
    }

    public String getObjectIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("objectId", String.class); // objectId 추출
    }
}
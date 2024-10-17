package kr.gateway.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Getter
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expired.access}")
    private long accessTokenExpiration;

    @Value("${jwt.expired.refresh}")
    private long refreshTokenExpiration;


    public Mono<String> generateToken(UserDetails userDetails, boolean isRefresh) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        return Mono.just(token);  // 생성된 토큰 반환
    }

    public long accessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Mono<Boolean> validateToken(String token) {
        try {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
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



//    public String getUserIdFromToken(String token) {
//        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
//        Key key = Keys.hmacShaKeyFor(keyBytes);
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)  // 서명 검증을 위한 key 설정
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();  // subject에 userId 혹은 username 저장
//    }

//    public Mono<String> refreshToken(String oldToken) {
//        if (!validateToken(oldToken).block()) {
//            return Mono.error(new RuntimeException("Invalid token"));
//        }
//
//        String userId = getUserIdFromToken(oldToken);
//        UserDetails userDetails = loadUserByUsername(userId);  // UserDetails를 가져오는 로직 필요
//        return generateToken(userDetails, true);  // 리프레시 토큰 생성
//    }

//    public Mono<Void> invalidateToken(String token) {
//        return Mono.empty();
//    }
//
//    public String getObjectIdFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get("objectId", String.class); // objectId 추출
//    }
}

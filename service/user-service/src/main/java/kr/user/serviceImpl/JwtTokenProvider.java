package kr.user.serviceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.user.document.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.validity.in.milliseconds}")
    private long validityInMilliseconds;

    public SecretKey generateKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Mono<String> createToken(User user) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.claims().setSubject(user.getId());
            claims.put("role", user.getRole());
            claims.put("nickname", user.getNickname());
            claims.put("username", user.getUsername());

            Date now = new Date();
            Date validity = new Date(now.getTime() + validityInMilliseconds);

            SecretKey key = generateKey();

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(validity)
                    .signWith(key)
                    .compact();
        });
    }

    // JWT 서명 및 구조 검증 메서드 (실제 검증)
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(generateKey()).parseClaimsJws(token);
            return true; // 서명 및 구조 검증 성공 시 true 반환
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false; // 검증 실패 시 false 반환
        }
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(generateKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getRole(String token) {
        return (String) Jwts.parser()
                .setSigningKey(generateKey())
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public String getUserId(String token) {
        return (String) Jwts.parser()
                .setSigningKey(generateKey())
                .parseClaimsJws(token)
                .getBody()
                .get("userId");
    }
}

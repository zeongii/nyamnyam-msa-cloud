package kr.gateway.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.gateway.config.UserDetailsImpl;
import kr.gateway.document.Token;
import kr.gateway.repository.TokenRepository;
import kr.gateway.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Getter
@Component
public class JwtTokenProvider {

    @Value("bywm4zC5-vR36j_mZPsd4jmNFUuny0XuYoln59AStsI=")
    private String secret;

    @Value("${jwt.expired.access}")
    private long accessTokenExpiration;

    @Value("${jwt.expired.refresh}")
    private long refreshTokenExpiration;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public JwtTokenProvider(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public Mono<String> generateToken(String userId, boolean isRefresh) {
        Date now = new Date();
        Date expiration;


        if (isRefresh) {
            expiration = new Date(now.getTime() + refreshTokenExpiration);
        } else {
            expiration = new Date(now.getTime() + accessTokenExpiration);
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();


        Token tokenEntity = Token.builder()
                .userId(userId)
                .token(token)
                .expirationDate(expiration)
                .isValid(true)
                .build();

        tokenRepository.save(tokenEntity).subscribe();

        return Mono.just(token);
    }

    public long accessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Mono<String> refreshToken(String oldToken) {

        return validateToken(oldToken)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new RuntimeException("Invalid refresh token"));
                    }

                    String userId = getUserIdFromToken(oldToken);
                    return generateToken(userId, false);
                });
    }

    public Mono<Boolean> validateToken(String token) {
        try {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

            return tokenRepository.findByToken(token)
                    .map(storedToken -> storedToken.getIsValid() && !storedToken.getExpirationDate().before(new Date()))
                    .defaultIfEmpty(false);

        } catch (ExpiredJwtException e) {
            System.out.println("Expired token");
            return Mono.just(false);
        } catch (SignatureException e) {
            System.out.println("Invalid token signature");
            return Mono.just(false);
        } catch (MalformedJwtException e) {
            System.out.println("Malformed token");
            return Mono.just(false);
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
            return Mono.just(false);
        }
    }


    public String getUserIdFromToken(String token) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }



    public Mono<UserDetails> loadUserByUserId(String userId) {
        return userRepository.findById(userId)
                .map(user -> new UserDetailsImpl(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                ));
    }


    public Mono<Void> invalidateToken(String token) {
        return tokenRepository.deleteByToken(token);
    }

    public String getObjectIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("objectId", String.class);
    }

}

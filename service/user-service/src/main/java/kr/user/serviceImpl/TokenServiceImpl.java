package kr.user.serviceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.user.document.Token;
import kr.user.service.TokenService;
import kr.user.repository.TokenRepository;
import kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.validity.in.milliseconds}")
    private long validityInMilliseconds;

    @Override
    public Mono<String> createAndSaveToken(String userId) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    return jwtTokenProvider.createToken(user)
                            .flatMap(token -> {
                                Token tokenEntity = Token.builder()
                                        .userId(userId)
                                        .token(token)
                                        .expirationDate(new Date(System.currentTimeMillis() + validityInMilliseconds))
                                        .isValid(true)
                                        .build();
                                return tokenRepository.save(tokenEntity).then(Mono.just(token));
                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }


    @Override
    public Mono<Boolean> validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(storedToken -> {
                    return storedToken.getIsValid() && !storedToken.getExpirationDate().before(new Date());
                })
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Void> logout(String token) {
        return tokenRepository.findByToken(token)
                .flatMap(storedToken -> {
                    storedToken.setIsValid(false);
                    return tokenRepository.save(storedToken);
                })
                .then();
    }

    @Override
    public Mono<String> refreshToken(String oldToken) {
        return tokenRepository.findByToken(oldToken)
                .flatMap(storedToken -> {
                    if (storedToken.getIsValid() && !storedToken.getExpirationDate().before(new Date())) {
                        return userRepository.findById(storedToken.getUserId())
                                .flatMap(user -> jwtTokenProvider.createToken(user)
                                        .flatMap(newToken -> {
                                            storedToken.setIsValid(false);
                                            return tokenRepository.save(storedToken)
                                                    .then(tokenRepository.save(Token.builder()
                                                            .userId(storedToken.getUserId())
                                                            .token(newToken)
                                                            .expirationDate(new Date(System.currentTimeMillis() + validityInMilliseconds))
                                                            .isValid(true)
                                                            .build()))
                                                    .then(Mono.just(newToken));
                                        }));
                    } else {
                        return Mono.error(new RuntimeException("Invalid token for refresh"));
                    }
                });
    }

    // 토큰에서 사용자 ID(username)를 추출하는 메서드
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtTokenProvider.generateKey())  // 서명 키를 사용해 JWT 파싱
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // JWT의 'sub' 필드에서 사용자 ID 추출
    }

}

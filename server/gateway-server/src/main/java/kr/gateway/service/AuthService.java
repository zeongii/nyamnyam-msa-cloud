package kr.gateway.service;

import kr.gateway.document.LoginRequest;
import kr.gateway.document.OAuth2Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthService {
    Mono<String> login(LoginRequest request);
    Mono<String> oauthLogin(OAuth2Request request);
    Mono<Boolean> validateToken(String token);
    String getUserIdFromToken(String token);
    Mono<String> refreshToken(String oldToken);  // 토큰 갱신
    Mono<Void> logout(String token);  // 로그아웃 처리
    Mono<Map<String, Object>> exchangeCodeForToken(String code, String state);
    Mono<ResponseEntity<String>> handleNaverCallback(String code, String state, ServerWebExchange exchange);
    Mono<ResponseEntity<Void>> redirectToNaverLogin(ServerWebExchange exchange);
}

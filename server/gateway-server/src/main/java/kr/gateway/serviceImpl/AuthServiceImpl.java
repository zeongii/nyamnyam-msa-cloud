package kr.gateway.serviceImpl;

import kr.gateway.document.LoginRequest;
import kr.gateway.document.OAuth2Request;
import kr.gateway.service.AuthService;
import kr.gateway.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<String> login(LoginRequest request) {
        return Mono.just(request.getUsername())
                .flatMap(username -> jwtTokenProvider.createToken(username));
    }

    @Override
    public Mono<String> oauthLogin(OAuth2Request request) {
        // 1. Null 체크
        if (request == null) {
            System.out.println("OAuth2Request is null");
            return Mono.error(new RuntimeException("OAuth2Request is null"));
        }

        String accessToken = request.getAccessToken();
        String provider = request.getProvider();

        // 2. Access Token 확인
        if (accessToken == null || accessToken.isEmpty()) {
            System.out.println("Access Token is missing: " + accessToken);
            return Mono.error(new RuntimeException("Access Token is null or empty"));
        }

        // 3. Provider 체크
        if (provider == null || provider.isEmpty()) {
            System.out.println("Provider is missing or invalid: " + provider);
            return Mono.error(new RuntimeException("Provider is null or empty"));
        }

        // 4. JWT 생성 시 예외 처리
        return jwtTokenProvider.createToken(provider)
                .doOnSuccess(token -> System.out.println("JWT 생성 성공: " + token))
                .doOnError(e -> System.out.println("JWT 생성 실패: " + e.getMessage()));
    }



    @Override
    public Mono<Boolean> validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @Override
    public Mono<String> refreshToken(String oldToken) {
        return jwtTokenProvider.refreshToken(oldToken);
    }

    @Override
    public Mono<Void> logout(String token) {
        return jwtTokenProvider.invalidateToken(token);
    }

    @Override
    public Mono<Map<String, Object>> exchangeCodeForToken(String code, String state) {
        String tokenUri = "https://nid.naver.com/oauth2.0/token";

        return webClient.post()
                .uri(tokenUri) // 전체 URI를 직접 사용
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=authorization_code&client_id=e2iaB9q3A_kk1k7hX6Qi" +
                        "&client_secret=Av6eAE_PsV&code=" + code + "&state=" + state +
                        "&redirect_uri=http://localhost:8000/auth/oauth2/code/naver")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }



    private Mono<Map<String, Object>> requestUserInfo(Map<String, Object> tokenResponse) {
        String accessToken = (String) tokenResponse.get("access_token");

        return webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    if (response.containsKey("response")) {
                        return Mono.just((Map<String, Object>) response.get("response"));
                    }
                    return Mono.error(new RuntimeException("Invalid user info response"));
                });
    }


    private String extractUserId(String response) {
        return response.split("\"id\":\"")[1].split("\"")[0];
    }

    private String extractUserNickname(String response) {
        return response.split("\"nickname\":\"")[1].split("\"")[0];
    }

    @Override
    public Mono<ResponseEntity<String>> handleNaverCallback(String code, String state, ServerWebExchange exchange) {
        return exchangeCodeForToken(code, state)
                .flatMap(tokenResponse -> requestUserInfo(tokenResponse))
                .flatMap(userInfo -> {
                    String username = (String) userInfo.get("id");
                    String objectId = (String) userInfo.get("id");

                    return jwtTokenProvider.createOAuthToken(username, objectId);
                })
                .map(jwt -> ResponseEntity.ok("OAuth2 Login successful. JWT: " + jwt))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage())));
    }





    public Mono<ResponseEntity<Void>> redirectToNaverLogin(ServerWebExchange exchange) {
        String state = UUID.randomUUID().toString();


        saveStateInSession(exchange, state);

        String authorizationUri = UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "e2iaB9q3A_kk1k7hX6Qi")
                .queryParam("redirect_uri", "http://localhost:8000/auth/oauth2/code/naver")
                .queryParam("state", state)
                .queryParam("scope", "email profile")
                .build()
                .toUriString();

        System.out.println("Redirecting to: " + authorizationUri);
        return Mono.just(ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", authorizationUri)
                .build());
    }


    private void saveStateInSession(ServerWebExchange exchange, String state) {
        exchange.getSession().doOnNext(session -> {
            session.getAttributes().put("oauth_state", state);
            System.out.println("Saved state in session: " + state); // 상태 저장 로그
        }).subscribe();
    }

    private Mono<Boolean> isStateValid(String state, ServerWebExchange exchange) {
        return exchange.getSession()
                .map(session -> {
                    String sessionState = session.getAttribute("oauth_state");
                    return sessionState != null && sessionState.equals(state);
                })
                .defaultIfEmpty(false);
    }
}

package kr.gateway.component;

import kr.gateway.document.LoginRequest;
import kr.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient = WebClient.create();
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Mono<ServerResponse> login(ServerRequest request) {
        System.out.println("Login request received");
        return request.bodyToMono(LoginRequest.class)
                .flatMap(req -> userRepository.findByUsername(req.getUsername())
                        .flatMap(user -> {
                            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                                return Mono.error(new RuntimeException("Invalid password"));
                            }

                            String role = user.getRole() != null ? user.getRole() : "USER";

                            return jwtTokenProvider.generateToken(user.getId(), false)
                                    .flatMap(jwt -> ServerResponse.ok().bodyValue("Login successful. JWT: " + jwt));
                        })
                        .switchIfEmpty(Mono.error(new RuntimeException("User not found"))))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Error: " + e.getMessage()));
    }

    public Mono<ServerResponse> refreshToken(ServerRequest request) {
        String authHeader = request.headers().firstHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Invalid token"));
        }

        String refreshToken = authHeader.substring(7);
        return jwtTokenProvider.refreshToken(refreshToken)
                .flatMap(newToken -> {
                    System.out.println("New Access Token generated: " + newToken);
                    return ServerResponse.ok().bodyValue(Collections.singletonMap("newAccessToken", newToken));
                })
                .onErrorResume(e -> {
                    System.out.println("Error occurred: " + e.getMessage());
                    return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(e.getMessage());
                });
    }

    public Mono<ServerResponse> logout(ServerRequest request) {
        String authHeader = request.headers().firstHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Invalid token"));
        }

        String token = authHeader.substring(7);
        return jwtTokenProvider.invalidateToken(token)
                .then(ServerResponse.noContent().build())
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    // 4. 네이버 로그인 리디렉션 처리
    public Mono<ServerResponse> redirectToNaverLogin(ServerRequest request) {
        String state = UUID.randomUUID().toString();
        saveStateInSession(request.exchange(), state);

        String authorizationUri = UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "e2iaB9q3A_kk1k7hX6Qi")
                .queryParam("redirect_uri", "http://localhost:8000/api/auth/oauth2/code/naver")
                .queryParam("state", state)
                .queryParam("scope", "profile")
                .build()
                .toUriString();

        return ServerResponse.temporaryRedirect(URI.create(authorizationUri)).build();
    }

    // 세션에 state 값 저장
    private void saveStateInSession(ServerWebExchange exchange, String state) {
        exchange.getSession().doOnNext(session -> {
            session.getAttributes().put("oauth_state", state);
            System.out.println("Saved state in session: " + state);
        }).subscribe();
    }

    public Mono<ServerResponse> handleNaverCallback(ServerRequest request) {
        String code = request.queryParam("code").orElse("");
        String state = request.queryParam("state").orElse("");

        return exchangeCodeForToken(code, state)
                .flatMap(this::requestUserInfo)
                .flatMap(naverUserInfo -> {
                    String naverUserId = (String) naverUserInfo.get("id");

                    // user-service로 네이버 사용자 ID로 회원 조회 요청
                    return registerUserInUserServiceAsync(naverUserInfo)  // 회원가입을 성공적으로 처리한 후
                            .then(issueJwtToken(naverUserInfo))               // 회원가입 성공 시 JWT 발급
                            .flatMap(jwt -> ServerResponse.ok().bodyValue("Login successful. JWT: " + jwt))
                            .onErrorResume(e -> {
                                System.out.println("Error during registration: " + e.getMessage());
                                return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Registration failed.");
                            });
                })
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Error: " + e.getMessage()));
    }


    private Mono<User> checkUserInUserService(String naverUserId) {
        return webClient.get()
                .uri("http://user/api/users/naver/{naverUserId}", naverUserId)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<Void> registerUserInUserServiceAsync(Map<String, Object> naverUserInfo) {
        System.out.println("회원 가입 요청 도달: " + naverUserInfo);

        return Mono.empty();  // 요청을 처리하지 않고 빈 응답 반환
    }

//    private Mono<Void> registerUserInUserServiceAsync(Map<String, Object> naverUserInfo) {
//        return webClient.post()
//                .uri("http://user-service/api/oauth/naver/signup")
//                .bodyValue(naverUserInfo)
//                .retrieve()
//                .bodyToMono(Void.class)
//                .onErrorResume(e -> {
//                    System.err.println("Failed to register user: " + e.getMessage());
//                    return Mono.empty();
//                });
//    }

    private Mono<String> issueJwtToken(Map<String, Object> naverUserInfo) {
        String userId = (String) naverUserInfo.get("id");

        // 여기에 로그 메시지만 출력하고 JWT 발급 부분은 주석 처리
        System.out.println("JWT 발급 예정: " + userId);

        // 실제 JWT 발급을 막고 '발급 예정'이라는 가짜 응답만 반환
        return Mono.just("JWT 발급 예정: " + userId);
    }


//    private Mono<String> issueJwtToken(Map<String, Object> naverUserInfo) {
//        String userId = (String) naverUserInfo.get("id");
//
//        String password = "NaverOAuthPassword";
//
//
//        UserDetails userDetails = new UserDetailsImpl(
//                userId,
//                password,
//                Collections.singletonList(new SimpleGrantedAuthority("USER"))
//        );
//
//        return jwtTokenProvider.generateToken(userId, false);
//    }




    // 네이버로 Access Token 요청
    private Mono<Map<String, Object>> exchangeCodeForToken(String code, String state) {
        String tokenUri = "https://nid.naver.com/oauth2.0/token";

        return webClient.post()
                .uri(tokenUri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=authorization_code&client_id=e2iaB9q3A_kk1k7hX6Qi" +
                        "&client_secret=Av6eAE_PsV&code=" + code + "&state=" + state +
                        "&redirect_uri=http://localhost:8000/api/auth/oauth2/code/naver")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});  // 제네릭 타입 명시
    }


    // 네이버에서 사용자 정보 요청
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

}

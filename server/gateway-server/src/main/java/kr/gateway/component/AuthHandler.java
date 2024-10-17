package kr.gateway.component;

import kr.gateway.component.JwtTokenProvider;
import kr.gateway.config.UserDetailsImpl;
import kr.gateway.document.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import org.springframework.core.ParameterizedTypeReference;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient = WebClient.create();

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(req -> {
                    // 대부분은 'USER' 역할로 설정하고, 특정 사용자에 대해 다른 역할을 부여
                    String role;
                    if ("adminUser".equals(req.getUsername())) {  // 'adminUser'를 관리자 계정으로 설정
                        role = "ADMIN";  // 특정 유저는 ADMIN 역할
                    } else {
                        role = "USER";  // 그 외의 유저는 USER 역할
                    }

                    // UserDetails에 역할 설정
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                    UserDetails userDetails = new UserDetailsImpl(req.getUsername(), req.getPassword(), authorities);

                    // JWT 생성
                    return jwtTokenProvider.generateToken(userDetails, false)
                            .flatMap(jwt -> ServerResponse.ok().bodyValue("Login successful. JWT: " + jwt));
                })
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Error: " + e.getMessage()));
    }





//
//    // 2. 토큰 갱신 처리
//    public Mono<ServerResponse> refreshToken(ServerRequest request) {
//        return request.bodyToMono(String.class)
//                .flatMap(jwtTokenProvider::refreshToken)
//                .flatMap(newToken -> ServerResponse.ok().bodyValue("New Token: " + newToken))
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(e.getMessage()));
//    }
//
//    // 3. 로그아웃 처리
//    public Mono<ServerResponse> logout(ServerRequest request) {
//        return request.bodyToMono(String.class)
//                .flatMap(jwtTokenProvider::invalidateToken)
//                .then(ServerResponse.noContent().build())
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
//    }

    // 4. 네이버 로그인 리디렉션 처리
    public Mono<ServerResponse> redirectToNaverLogin(ServerRequest request) {
        String state = UUID.randomUUID().toString();
        saveStateInSession(request.exchange(), state);

        String authorizationUri = UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "e2iaB9q3A_kk1k7hX6Qi")
                .queryParam("redirect_uri", "http://localhost:8000/auth/oauth2/code/naver")
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
                    Mono<Void> registerUser = registerUserInUserServiceAsync(naverUserInfo); // 비동기 회원가입 처리

                    // JWT 발급과 동시에 회원가입 비동기로 진행
                    return issueJwtToken(naverUserInfo)
                            .flatMap(jwt -> ServerResponse.ok().bodyValue("Login successful. JWT: " + jwt))
                            .doOnSuccess(res -> registerUser.subscribe()); // 비동기 회원가입 처리 실행
                })
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Error: " + e.getMessage()));
    }

    // user-service로 네이버 사용자 조회
    private Mono<User> checkUserInUserService(String naverUserId) {
        return webClient.get()
                .uri("http://user/api/users/naver/{naverUserId}", naverUserId)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(e -> Mono.empty());  // 오류 처리: 유저가 없으면 빈 값 반환
    }

    // user-service로 네이버 사용자 자동 회원가입 요청을 비동기로 처리
    private Mono<Void> registerUserInUserServiceAsync(Map<String, Object> naverUserInfo) {
        return webClient.post()
                .uri("http://user/api/users")
                .bodyValue(naverUserInfo)
                .retrieve()
                .bodyToMono(Void.class) // 비동기 처리이므로 반환값 없음
                .onErrorResume(e -> {
                    // 실패 시 로깅 가능
                    System.err.println("Failed to register user: " + e.getMessage());
                    return Mono.empty();  // 오류 발생 시 빈 값 반환
                });
    }


    private Mono<String> issueJwtToken(Map<String, Object> naverUserInfo) {
        String username = (String) naverUserInfo.get("nickname");
        String password = "NaverOAuthPassword";  // OAuth 비밀번호는 더미 값 사용

        // 기본적으로 USER 역할 부여
        UserDetails userDetails = new UserDetailsImpl(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );

        // JWT 생성
        return jwtTokenProvider.generateToken(userDetails, false);
    }



    // 네이버로 Access Token 요청
    private Mono<Map<String, Object>> exchangeCodeForToken(String code, String state) {
        String tokenUri = "https://nid.naver.com/oauth2.0/token";

        return webClient.post()
                .uri(tokenUri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=authorization_code&client_id=e2iaB9q3A_kk1k7hX6Qi" +
                        "&client_secret=Av6eAE_PsV&code=" + code + "&state=" + state +
                        "&redirect_uri=http://localhost:8000/auth/oauth2/code/naver")
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

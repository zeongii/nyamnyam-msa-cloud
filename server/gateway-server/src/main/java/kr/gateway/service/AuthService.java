package kr.gateway.service;//package kr.gateway.serviceImpl;
//
//import kr.gateway.component.JwtTokenProvider;
//import kr.gateway.document.LoginRequest;
//
//import kr.gateway.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.util.UriComponentsBuilder;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final WebClient webClient = WebClient.create();
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public Mono<String> login(LoginRequest request) {
//        return Mono.just(request.getUsername())
//                .flatMap(username -> jwtTokenProvider.createToken(username));
//    }
//
//    @Override
//    public Mono<Boolean> validateToken(String token) {
//        return jwtTokenProvider.validateToken(token);
//    }
//
//    @Override
//    public String getUserIdFromToken(String token) {
//        return jwtTokenProvider.getUserIdFromToken(token);
//    }
//
//    @Override
//    public Mono<String> refreshToken(String oldToken) {
//        return jwtTokenProvider.refreshToken(oldToken);
//    }
//
//    @Override
//    public Mono<Void> logout(String token) {
//        return jwtTokenProvider.invalidateToken(token);
//    }
//
//    public Mono<ResponseEntity<Void>> redirectToNaverLogin(ServerWebExchange exchange) {
//        String state = UUID.randomUUID().toString();
//
//        saveStateInSession(exchange, state);
//
//        String authorizationUri = UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/authorize")
//                .queryParam("response_type", "code")
//                .queryParam("client_id", "e2iaB9q3A_kk1k7hX6Qi")
//                .queryParam("redirect_uri", "http://localhost:8000/auth/oauth2/code/naver")
//                .queryParam("state", state)
//                .queryParam("scope", "email profile")
//                .build()
//                .toUriString();
//
//        return Mono.just(ResponseEntity.status(HttpStatus.FOUND)
//                .header("Location", authorizationUri)
//                .build());
//    }
//
//    private void saveStateInSession(ServerWebExchange exchange, String state) {
//        exchange.getSession().doOnNext(session -> {
//            session.getAttributes().put("oauth_state", state);
//            System.out.println("Saved state in session: " + state);
//        }).subscribe();
//    }
//}

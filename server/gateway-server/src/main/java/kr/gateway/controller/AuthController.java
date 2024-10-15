package kr.gateway.controller;

import kr.gateway.document.LoginRequest;
import kr.gateway.document.OAuth2Request;
import kr.gateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest request) {
        return authService.login(request)
                .map(jwt -> ResponseEntity.ok("Login successful. JWT: " + jwt))
                .onErrorResume(e -> {

                    System.out.println("Login error: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()));
                });
    }

    @PostMapping("/oauth2")
    public Mono<ResponseEntity<String>> oauthLogin(@RequestBody OAuth2Request request) {
        return authService.oauthLogin(request)
                .map(jwt -> ResponseEntity.ok("OAuth2 Login successful. JWT: " + jwt))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).body(e.getMessage())));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<String>> refreshToken(@RequestBody String oldToken) {
        return authService.refreshToken(oldToken)
                .map(newToken -> ResponseEntity.ok("New Token: " + newToken))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).body(e.getMessage())));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Object>> logout(@RequestBody String token) {
        return authService.logout(token)
                .thenReturn(ResponseEntity.noContent().build())
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).build()));
    }

    @GetMapping("/oauth2/code/naver")
    public Mono<ResponseEntity<String>> handleNaverCallback(@RequestParam String code,
                                                            @RequestParam String state,
                                                            ServerWebExchange exchange) {
        return authService.handleNaverCallback(code, state, exchange);
    }

    @GetMapping("/oauth2/authorization/naver")
    public Mono<ResponseEntity<Void>> redirectToNaverLogin(ServerWebExchange exchange) {
        return authService.redirectToNaverLogin(exchange);
    }

}

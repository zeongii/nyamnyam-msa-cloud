package kr.gatewayserver.config;

import kr.user.service.UserService;
import kr.user.serviceImpl.TokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final TokenServiceImpl tokenService;  // TokenService로 변경
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // OPTIONS 요청일 경우 바로 다음 필터로 넘기기
        if (exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
            return chain.filter(exchange);
        }

        String requestPath = exchange.getRequest().getPath().value();

        // 회원 가입 및 로그인 경로는 인증 없이 처리
        return ("/api/user/join".equals(requestPath) || "/api/user/login".equals(requestPath))
                ? chain.filter(exchange)
                : Mono.justOrEmpty(resolveToken(exchange.getRequest()))
                .flatMap(tokenService::validateToken)  // 변경된 validateToken 메서드 사용
                .flatMap(isValid -> {
                    if (!isValid) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return Mono.empty();
                    }

                    String username = tokenService.getUsernameFromToken(resolveToken(exchange.getRequest())); // 토큰에서 사용자 ID 가져오기
                    System.out.println("Extracted Username: " + username);

                    return userService.findById(username) // username은 실제로 ID이므로 findById 사용
                            .flatMap(user -> {
                                if (user == null) {
                                    System.out.println("User not found for ID: " + username);
                                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                    return Mono.empty();
                                }
                                System.out.println("User found: " + user.getUsername());
                                exchange.getAttributes().put("userDetails", user);
                                return chain.filter(exchange);
                            });
                });
    }

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        return (bearerToken != null && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
    }
}

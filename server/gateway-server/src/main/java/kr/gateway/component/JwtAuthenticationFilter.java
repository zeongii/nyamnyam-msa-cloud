//package kr.gateway.config;
//
//import kr.gateway.service.AuthService; // 새롭게 AuthService로 통합
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter implements WebFilter {
//
//    private final AuthService authService;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        if (exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
//            return chain.filter(exchange);
//        }
//
//        String path = exchange.getRequest().getPath().value();
//        if ("/auth/login".equals(path) || "/auth/oauth2/naver".equals(path)) {
//            return chain.filter(exchange);
//        }
//
//        return Mono.justOrEmpty(resolveToken(exchange.getRequest()))
//                .flatMap(authService::validateToken)
//                .flatMap(isValid -> {
//                    if (!isValid) {
//                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                        return Mono.empty();
//                    }
//
//                    String userId = authService.getUserIdFromToken(resolveToken(exchange.getRequest()));
//                    return authService.findUserById(userId)
//                            .flatMap(user -> {
//                                exchange.getAttributes().put("userDetails", user); // userDetails를 Exchange의 속성에 추가
//                                return chain.filter(exchange);
//                            });
//                })
//                .switchIfEmpty(Mono.defer(() -> {
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return Mono.empty();
//                }));
//    }
//
//
//    private String resolveToken(ServerHttpRequest request) {
//        String bearerToken = request.getHeaders().getFirst("Authorization");
//        return (bearerToken != null && bearerToken.startsWith("Bearer "))
//                ? bearerToken.substring(7) : null;
//    }
//}
//

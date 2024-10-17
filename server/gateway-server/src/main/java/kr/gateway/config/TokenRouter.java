package kr.gateway.config;

import kr.gateway.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;

@Configuration
@RequiredArgsConstructor
public class TokenRouter {

    private final TokenService tokenService;

    @Bean
    public RouterFunction<ServerResponse> tokenRoutes() {
        return route()
                .POST("/api/token/create", this::createToken)
                .POST("/api/token/validate", this::validateToken)
                .POST("/api/token/logout", this::logout)
                .POST("/api/token/refresh", this::refreshToken)
                .build();
    }

    private Mono<ServerResponse> createToken(ServerRequest request) {
        return request.queryParam("userId")
                .map(userId -> tokenService.createAndSaveToken(userId)
                        .flatMap(token -> ok().bodyValue(token)))
                .orElse(badRequest().bodyValue("Missing userId parameter"));
    }

    private Mono<ServerResponse> validateToken(ServerRequest request) {
        return request.queryParam("token")
                .map(token -> tokenService.validateToken(token)
                        .flatMap(valid -> ok().bodyValue(valid)))
                .orElse(badRequest().bodyValue("Missing token parameter"));
    }

    private Mono<ServerResponse> logout(ServerRequest request) {
        return request.bodyToMono(Map.class)
                .flatMap(body -> {
                    String token = (String) body.get("token");
                    return tokenService.logout(token)
                            .then(ok().build());
                });
    }

    private Mono<ServerResponse> refreshToken(ServerRequest request) {
        return request.queryParam("oldToken")
                .map(oldToken -> tokenService.refreshToken(oldToken)
                        .flatMap(token -> ok().bodyValue(token)))
                .orElse(badRequest().bodyValue("Missing oldToken parameter"));
    }
}

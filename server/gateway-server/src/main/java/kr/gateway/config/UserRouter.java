package kr.gateway.config;

import kr.gateway.document.User;
import kr.gateway.document.UserModel;
import kr.gateway.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerRequest;

import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;

@Configuration
@RequiredArgsConstructor
public class UserRouter {

    private final UserService userService;

    @Bean
    public RouterFunction<ServerResponse> userRoutes() {
        return route()
                .GET("/api/user/existsById", this::existsById)
                .GET("/api/user/findById", this::findById)
                .POST("/api/user/login", this::login)
                .GET("/api/user/check-username", this::checkUsername)
                .build();
    }

    private Mono<ServerResponse> existsById(ServerRequest request) {
        return request.queryParam("id")
                .map(id -> userService.existsById(id)
                        .flatMap(exists -> ok().bodyValue(exists)))
                .orElse(badRequest().bodyValue("Missing id parameter"));
    }

    private Mono<ServerResponse> findById(ServerRequest request) {
        return request.queryParam("id")
                .map(id -> userService.findById(id)
                        .flatMap(user -> ok().bodyValue(user)))
                .orElse(badRequest().bodyValue("Missing id parameter"));
    }

    private Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(UserModel.class)
                .flatMap(user -> {
                    String username = user.getUsername();
                    String password = user.getPassword();
                    return userService.authenticate(username, password)
                            .flatMap(token -> ok().bodyValue(token));
                })
                .onErrorResume(e -> badRequest().bodyValue("Error: " + e.getMessage()));
    }

    private Mono<ServerResponse> checkUsername(ServerRequest request) {
        return request.queryParam("username")
                .map(username -> userService.findByUsername(username)
                        .hasElement()
                        .flatMap(exists -> ok().bodyValue(exists)))
                .orElse(badRequest().bodyValue("Missing username parameter"));
    }
}

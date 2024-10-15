package kr.gateway.component;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class CustomServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        String requestPath = webFilterExchange.getExchange().getRequest().getPath().toString();

        if (requestPath.startsWith("/auth/login")) {
            // 일반 로그인 성공 시 리다이렉트
            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/dashboard"));
        } else if (requestPath.startsWith("/auth/oauth2")) {
            // OAuth2 로그인 성공 시 리다이렉트
            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/oauth2-success"));
        }

        return Mono.empty();  // 필터 체인 종료
    }

}



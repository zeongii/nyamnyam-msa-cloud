package kr.gatewayserver.component;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return null;
    }
}

package kr.gateway.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        log.info("WebFilterExchange 정보 : " + exchange);
        log.info("Authentication 정보 : " + authentication);
        log.info("Authorities 정보 : " + authentication.getAuthorities());
        log.info("Credentials 정보 : " + authentication.getPrincipal());

        if (requestPath.startsWith("/auth/login", "/auth/oauth2")) {
            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("http://localhost:3000"));
            exchange.getExchange().getResponse().getHeaders().add("Content-Type", "application/json");
            exchange.getExchange().getResponse()
                    .writeWith(
                            jwtTokenProvider.generateToken(null, false) // 토큰 발급
                                    .doOnNext(accessToken ->
                                            exchange
                                                    .getExchange()
                                                    .getResponse()
                                                    .getCookies()
                                                    .add("accessToken",
                                                            ResponseCookie.from("accessToken")
                                                                    .path("/")
                                                                    .maxAge(jwtTokenProvider.getAccessTokenExpired())
                                                                    .build())
                                    )
                    ) // 쿠키 생성 및 설정
                    .flatMap(i -> jwtTokenProvider.generateToken(null, true)) // 리프레시 토큰 발급
                    .doOnNext(signalType -> log.info("OAuth2 Login Success"))   // 로그 출력
                    .flatMap(signalType -> exchange.getExchange().getResponse().setComplete())
                    .flatMap(signalType -> Mono.empty())
            );
        }else{
            // 지정된 url 이 아닌 경우
            return  Mono.empty();  // 필터 체인 종료
        }


    }

}
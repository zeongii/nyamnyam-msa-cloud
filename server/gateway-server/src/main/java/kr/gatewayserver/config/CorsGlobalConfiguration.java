package kr.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 허용할 Origin 설정
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        config.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        config.setAllowCredentials(true); // 인증 관련 정보 허용

        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 정책 적용
        return new CorsWebFilter(source);
    }
}

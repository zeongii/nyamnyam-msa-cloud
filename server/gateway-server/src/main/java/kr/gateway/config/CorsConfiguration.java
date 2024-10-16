package kr.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000")); // 허용할 Origin 설정
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        config.setExposedHeaders(Arrays.asList("*")); // 클라이언트에서 접근할 수 있는 응답 헤더
        config.setAllowCredentials(true); // 인증 관련 정보 허용

        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 정책 적용
        return new CorsWebFilter(source);
    }

}

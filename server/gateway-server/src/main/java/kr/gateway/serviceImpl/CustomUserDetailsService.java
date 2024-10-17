package kr.gateway.serviceImpl;

import kr.gateway.config.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private WebClient webClient;  // WebClient 주입

    @Override
    public UserDetails loadUserByUsername(String username) {
        // user-service에 요청하여 사용자 정보를 가져온다 (필요한 데이터만 가져오기)
        Mono<Map<String, Object>> userMono = webClient.get()
                .uri("http://user-service/api/users/{username}", username)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> userInfo = userMono.block();

        if (userInfo == null || userInfo.get("username") == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        String userRole = (String) userInfo.get("role");
        String userPassword = (String) userInfo.get("password");

        return new UserDetailsImpl(
                (String) userInfo.get("username"),
                userPassword,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole))
        );
    }

}

package kr.user.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.user.absent.NaverMemberInfo;
import kr.user.document.User;
import kr.user.repository.UserRepository;
import kr.user.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OAuth2ServiceImpl implements OAuth2Service {

    private final String clientId = "e2iaB9q3A_kk1k7hX6Qi";
    private final String clientSecret = "Av6eAE_PsV";
    private final String redirectUri = "http://localhost:8080/oauth2/callback";
    private final String tokenUri = "https://nid.naver.com/oauth2.0/token";
    private final String userInfoUri = "https://openapi.naver.com/v1/nid/me";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 액세스 토큰 받기
    @Override
    public Mono<String> getAccessToken(String code) {
        String requestBody = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code" +
                "&code=" + code;

        return webClientBuilder.build()
                .post()
                .uri(tokenUri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseAccessToken);
    }

    // 사용자 정보 요청
    @Override
    public Mono<NaverMemberInfo> getUserInfo(String accessToken) {
        return webClientBuilder.build()
                .get()
                .uri(userInfoUri + "?access_token=" + accessToken)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseUserInfo);
    }

    // 사용자 정보를 User로 변환하고 저장
    @Override
    public Mono<String> handleNaverLogin(String code) {
        return getAccessToken(code)
                .flatMap(this::getUserInfo)
                .flatMap(naverMemberInfo -> {
                    // 사용자 존재 여부 확인
                    return userRepository.findByUsername(naverMemberInfo.getEmail())
                            .switchIfEmpty(Mono.defer(() -> {
                                // 새 사용자 등록
                                User newUser = User.builder()
                                        .username(naverMemberInfo.getEmail())
                                        .name(naverMemberInfo.getName())
                                        .nickname(naverMemberInfo.getName())
                                        .enabled(true)
                                        .role("ROLE_USER")
                                        .build();
                                return userRepository.save(newUser); // 새 사용자 저장
                            }))
                            .flatMap(user -> {
                                // JWT 발급 (User 객체 전달)
                                String jwtToken = jwtTokenProvider.createToken(user).block();
                                return Mono.just(jwtToken); // JWT 반환
                            });
                });
    }


    private String parseAccessToken(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing access token", e);
        }
    }

    private NaverMemberInfo parseUserInfo(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return new NaverMemberInfo(objectMapper.convertValue(jsonNode, Map.class));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing user info", e);
        }
    }
}

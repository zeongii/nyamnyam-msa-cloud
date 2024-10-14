package kr.user.controller;

import kr.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/create")
    public Mono<String> createToken(@RequestParam String userId) {
        return tokenService.createAndSaveToken(userId);
    }

    @PostMapping("/validate")
    public Mono<Boolean> validateToken(@RequestParam String token) {
        return tokenService.validateToken(token);
    }

    @PostMapping("/logout")
    public Mono<Void> logout(@RequestBody Map<String, String> request) {
        String token = request.get("token"); // POST 바디에서 토큰 추출
        return tokenService.logout(token);
    }

    @PostMapping("/refresh")
    public Mono<String> refreshToken(@RequestParam String oldToken) {
        return tokenService.refreshToken(oldToken);
    }
}

package kr.user.controller;

import kr.user.document.UserScore;
import kr.user.service.UserScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class UserScoreController {

    private final UserScoreService userScoreService;

    @PostMapping("/scoreUp")
    public Mono<UserScore> scoreUp(@RequestBody UserScore userScore) {
        return userScoreService.save(userScore);
    }

    @PostMapping("/scoreDown")
    public Mono<UserScore> scoreDown(@RequestBody UserScore userScore) {
        return userScoreService.save(userScore);
    }

    @GetMapping("/user/{userId}")
    public Flux<UserScore> getUserScores(@PathVariable String userId) {
        return userScoreService.findByUserId(userId);
    }

    @GetMapping("/average/{userId}")
    public Mono<Double> getUserAverageScore(@PathVariable String userId) {
        return userScoreService.calculateUserAverageScore(userId);
    }
}

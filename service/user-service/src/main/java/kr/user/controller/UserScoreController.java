package kr.user.controller;

import kr.user.document.UserScore;
import kr.user.service.UserScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/score")
public class UserScoreController {

    private final UserScoreService userScoreService;

    @PostMapping("/score")
    public Mono<UserScore> score(@RequestBody UserScore userScore) {
        return userScoreService.save(userScore);
    }

    @GetMapping("/user/{scoreUserId}")
    public Flux<UserScore> getUserScores(@PathVariable String scoreUserId) {
        return userScoreService.findByScoreUserId(scoreUserId);
    }

    @GetMapping("/average/{scoreUserId}")
    public Mono<Double> getUserAverageScore(@PathVariable String scoreUserId) {
        return userScoreService.calculateUserAverageScore(scoreUserId);
    }
}


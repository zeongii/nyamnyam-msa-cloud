package kr.user.service;

import kr.user.document.UserScore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserScoreService {
    Mono<UserScore> save(UserScore userScore);
    Flux<UserScore> findByUserId(String userId);
    Mono<Double> calculateUserAverageScore(String scoreUserId);
}

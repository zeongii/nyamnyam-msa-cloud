package kr.user.serviceImpl;

import kr.user.document.UserScore;
import kr.user.repository.UserScoreRepository;
import kr.user.service.UserScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserScoreServiceImpl implements UserScoreService {

    private final UserScoreRepository userScoreRepository;

    @Override
    public Mono<UserScore> save(UserScore userScore) {
        return userScoreRepository.save(userScore);
    }

    @Override
    public Flux<UserScore> findByUserId(String userId) {
        return userScoreRepository.findByUserId(userId);
    }

    @Override
    public Mono<Double> calculateUserAverageScore(String scoreUserId) {
        return findByUserId(scoreUserId)
                .map(UserScore::getScore)
                .collectList()
                .map(scores -> scores.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0));
    }
}

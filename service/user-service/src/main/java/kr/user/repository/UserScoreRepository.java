package kr.user.repository;

import kr.user.document.UserScore;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserScoreRepository extends ReactiveMongoRepository<UserScore, String> {
    Flux<UserScore> findByUserId(String scoreUserId);
}

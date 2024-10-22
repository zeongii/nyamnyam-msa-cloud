package kr.gateway.repository;

import kr.gateway.document.Token;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TokenRepository extends ReactiveMongoRepository<Token, String> {
    Mono<Token> findByToken(String token);
    Mono<Void> deleteByToken(String token);
}
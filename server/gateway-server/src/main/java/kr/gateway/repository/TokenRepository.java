package kr.gateway.repository;

import kr.gateway.document.Token;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TokenRepository extends ReactiveMongoRepository<Token, String> {
    Mono<Token> findByToken(String token);
    Mono<Void> deleteByToken(String token);
}
package kr.gateway.repository;


import kr.gateway.document.Token;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TokenRepository extends ReactiveMongoRepository<Token, String> {

    // 토큰으로 검색
    Mono<Token> findByToken(String token);

    // 사용자 ID로 검색
    Mono<Token> findByUserId(String userId);

    // 토큰 삭제 (옵션)
    Mono<Void> deleteByToken(String token);
}

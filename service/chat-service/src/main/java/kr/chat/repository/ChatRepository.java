package kr.chat.repository;


import kr.chat.document.Chat;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {

    @Tailable //커서를 안닫고 계속 유지한다.
    @Query("{sender: ?0,chatRoomId: ?1}")
    Flux<Chat> mFindBySender(String sender, String chatRoomId);// Flux 흐름 response를 유지하면서 계속 흘려보내기

    @Tailable
    @Query("{chatRoomId: ?0}")
    Flux<Chat> mFindByChannelId(String chatRoomId);

    Flux<Chat> findByChatRoomId(String chatRoomId);


}

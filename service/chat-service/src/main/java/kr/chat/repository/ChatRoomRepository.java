package kr.chat.repository;


import kr.chat.document.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    // 채팅방의 participants 필드에 nickname이 포함된 채팅방 목록을 조회
    Flux<ChatRoom> findByParticipantsContains(String nickname);

}

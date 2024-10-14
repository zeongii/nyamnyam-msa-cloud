package kr.chat.serviceImpl;



import kr.chat.document.ChatRoom;
import kr.chat.repository.ChatRoomRepository;
import kr.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;


    @Override
    public Mono<ChatRoom> save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public Flux<ChatRoom> findAllByNickname(String nickname) {
        // nickname이 null이면 전체 ChatRoom 조회, 아니면 nickname으로 필터링하여 조회
        if (nickname == null || nickname.isEmpty()) {
            return chatRoomRepository.findAll();
        }
        return chatRoomRepository.findByParticipantsContains(nickname);
    }


    @Override
    public Mono<ChatRoom> findById(String id) {
        return chatRoomRepository.findById(id);
    }

    @Override
    public Mono<ChatRoom> updateChatRoom(String id, ChatRoom chatRoom) {
        return chatRoomRepository.findById(id)
                .flatMap(existingChatRoom -> {
                    existingChatRoom.setName(chatRoom.getName());
                    existingChatRoom.setParticipants(chatRoom.getParticipants());
                    existingChatRoom.setMessages(chatRoom.getMessages());
                    return chatRoomRepository.save(existingChatRoom);
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return chatRoomRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return chatRoomRepository.existsById(id);
    }

    @Override
    public Mono<Long> count() {
        return chatRoomRepository.count();
    }

    @Override
    public Flux<ChatRoom> crawling() {
        return null;
    }



}
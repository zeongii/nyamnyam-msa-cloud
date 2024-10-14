package kr.chat.service;



import kr.chat.document.Chat;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {
    Mono<String> uploadFile(MultipartFile file);

    Flux<Chat> mFindBySender(String sender, String chatRoomId);

    Flux<Chat> mFindByChatRoomId(String chatRoomId);

    Mono<Chat> saveMessage(Chat chat);


    Mono<Long> getUnreadMessageCountByChatRoomId(String chatRoomId, String nickname);

    Mono<Long> getParticipantsNotReadCount(String chatId);

    Mono<Chat> markAsRead(String chatId, String nickname);

    Mono<Chat> updateReadBy(String chatId, String nickname);

}

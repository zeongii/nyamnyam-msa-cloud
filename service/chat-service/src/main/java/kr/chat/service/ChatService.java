package kr.chat.service;

import kr.chat.document.Chat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {

    Flux<Chat> mFindByChatRoomId(String chatRoomId);

    Mono<Chat> saveMessage(Chat chat);

    Mono<Long> getUnreadMessageCountByChatRoomId(String chatRoomId, String nickname);

    Mono<Long> getParticipantsNotReadCount(String chatId);

    Mono<Chat> markAsRead(String chatId, String nickname);

    Mono<Chat> updateReadBy(String chatId, String nickname);

}

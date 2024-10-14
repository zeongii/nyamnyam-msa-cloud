package kr.chat.service;


import kr.chat.document.ChatRoom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ChatRoomService {

    Mono<ChatRoom> save(ChatRoom chatRoom);

    Flux<ChatRoom> findAllByNickname(String nickname);

    Mono<ChatRoom> findById(String id);

    Mono<ChatRoom> updateChatRoom(String id, ChatRoom chatRoom);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsById(String id);

    Mono<Long> count();


    Flux<ChatRoom> crawling();



}

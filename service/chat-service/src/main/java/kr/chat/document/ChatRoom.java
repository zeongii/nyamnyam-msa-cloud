package kr.chat.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "chatRooms")
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    private String id;
    private String name;

    // 내장된 참가자 리스트
    private List<String> participants; //참가자 리스트

    // 내장된 채팅 메시지 리스트
    private List<Chat> messages;


}
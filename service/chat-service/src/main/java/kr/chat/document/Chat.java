package kr.chat.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "chats")
@AllArgsConstructor
@Builder
public class Chat {
    @Id
    private String id;

    private String message; // 채팅 내용
    private LocalDateTime createdAt;
    private String sender;  // 발신자 nickname
    private String chatRoomId;


    // 참가자 읽음 상태 (참가자 수에 따라 크기가 달라질 수 있음)
    private Map<String, Boolean> readBy; // key: 참가자 닉네임, value: 읽음 여부
    // 추가: 총 참가자 수 (읽지 않은 메시지 수를 계산할 때 필요)
    private Long totalParticipants;


}

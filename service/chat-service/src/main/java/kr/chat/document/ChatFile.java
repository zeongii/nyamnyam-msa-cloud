package kr.chat.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatFiles") // MongoDB 컬렉션과 매핑
public class ChatFile {
    @Id
    private String id;

    private String chatRoomId;
    private String sender;
    private String fileName;
    private String fileUrl;
    private long size;
    private String contentType; // 파일 형식 (예: image/jpeg)
}

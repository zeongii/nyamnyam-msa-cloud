package kr.user.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "usersThumbnails")
@AllArgsConstructor
@Builder
public class UsersThumbnail {

    @Id
    private String id;
    private String userId;
    private String thumbnailUrl;
    private Long createdAt;
}

package kr.user.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "score")
@AllArgsConstructor
@Builder
public class UserScore {

    @Id
    private String id;
    private String userId;
    private String scoreUserId;
    private Double score;
    private String comment;
}

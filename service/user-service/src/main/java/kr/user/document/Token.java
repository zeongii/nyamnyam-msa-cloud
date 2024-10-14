package kr.user.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "tokens")
@AllArgsConstructor
@Builder
public class Token {

    @Id
    private String id;
    private String userId;
    private String token;
    private Date expirationDate;
    private Boolean isValid;

}

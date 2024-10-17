package kr.gateway.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String name;
    private Long age;
    private String role;
    private String tel;
    private String gender;
    private Boolean enabled;
    private String imgId;
    private Double score;

}

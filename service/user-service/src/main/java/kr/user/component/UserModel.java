package kr.user.component;

import lombok.Data;

@Data
public class UserModel {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String name;
    private Long age;
    private String role;
    private String tel;
    private String gender;
    private Boolean enabled;


}

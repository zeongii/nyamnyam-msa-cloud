package kr.user.absent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NaverMemberInfo implements OAuth2MemberInfo {
    private Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        // "response" 맵에서 "id"를 가져옵니다.
        return (String) ((Map<String, Object>) attributes.get("response")).get("id");
    }

    @Override
    public String getProvider() {
        return "naver"; // 제공자 이름
    }

    @Override
    public String getName() {
        // "response" 맵에서 "name"을 가져옵니다.
        return (String) ((Map<String, Object>) attributes.get("response")).get("name");
    }

    @Override
    public String getEmail() {
        // "response" 맵에서 "email"을 가져옵니다.
        return (String) ((Map<String, Object>) attributes.get("response")).get("email");
    }
}

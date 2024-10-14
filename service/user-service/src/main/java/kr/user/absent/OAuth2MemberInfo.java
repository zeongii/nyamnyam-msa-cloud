package kr.user.absent;

public interface OAuth2MemberInfo {
    String getProviderId(); // OAuth 제공자 ID
    String getProvider();    // OAuth 제공자 이름
    String getName();        // 사용자 이름
    String getEmail();       // 사용자 이메일
}

package kr.admin.component;

import lombok.Data;

@Data
public class FollowModel {
    private Long id;

    // 나를 팔로우하는 사람
    private String follower;

    // 내가 팔로우하는 사람
    private String following;
}

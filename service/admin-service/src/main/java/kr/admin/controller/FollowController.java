package kr.admin.controller;

import kr.admin.component.FollowModel;
import kr.admin.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/{follower}/{following}/isFollow")
    public ResponseEntity<Boolean> isFollow(@PathVariable String follower, @PathVariable String following) {
        return ResponseEntity.ok(followService.findFollowingByFollower(follower,following));
    }

    @PostMapping("")
    public ResponseEntity<?> follow(@RequestBody FollowModel follow) {
        return ResponseEntity.ok(followService.follow(follow));
    }

    @DeleteMapping("/{follower}/{following}")
    public ResponseEntity<Boolean> unfollow(@PathVariable String follower, @PathVariable String following) {
        return ResponseEntity.ok(followService.unfollow(follower, following));
    }

    // 나를 팔로우하는 목록 보기
    @GetMapping("/findMyFollower/{nickname}")
    public ResponseEntity<List<?>> fingMyFollower(@PathVariable String nickname) {
        return ResponseEntity.ok(followService.findMyFollower(nickname));
    }

    // 내가 팔로우하는 목록 보기
    @GetMapping("/findMyFollowing/{nickname}")
    public ResponseEntity<List<?>> findMyFollowing(@PathVariable String nickname) {
        return ResponseEntity.ok(followService.findMyFollowing(nickname));
    }
}

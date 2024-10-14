package kr.user.controller;

import kr.user.document.User;
import kr.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/existsById")
    public Mono<Boolean> existsById(@RequestParam String id) {
        return userService.existsById(id);
    }

    @GetMapping("/findById")
    public Mono<User> findById(@RequestParam String id) {
        return userService.findById(id);
    }

    @GetMapping("/findAll")
    public Flux<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/count")
    public Mono<Long> count() {
        return userService.count();
    }

    @DeleteMapping("/deleteById")
    public Mono<Void> deleteById(@RequestParam String id) {
        return userService.deleteById(id);
    }

    @PutMapping("/update")
    public Mono<User> update(@RequestPart("user") User user, @RequestPart(value = "thumbnails", required = false) List<MultipartFile> thumbnails) {
        return userService.update(user, thumbnails);
    }

    @PostMapping("/join")
    public Mono<User> join(@RequestPart("user") User user,
                           @RequestPart(name = "thumbnails", required = false) List<MultipartFile> thumbnails) {
        return userService.save(user, thumbnails     != null ? thumbnails : Collections.emptyList());
    }


    @PostMapping("/login")
    public Mono<String> login(@RequestParam String username, @RequestParam String password) {
        return userService.authenticate(username, password);
    }

    @GetMapping("/check-username")
    public Mono<Boolean> checkUsername(@RequestParam String username) {
        return userService.findByUsername(username)
                .hasElement();
    }

}

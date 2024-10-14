package kr.user.controller;

import kr.user.document.User;
import kr.user.service.UserThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/thumbnails")
public class UserThumbnailController {

    private final UserThumbnailService userThumbnailService;

    @PostMapping("/upload")
    public Mono<ResponseEntity<Void>> uploadThumbnail(@RequestBody User user, @RequestParam("images") List<MultipartFile> images) {
        return userThumbnailService.uploadThumbnail(user, images)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}

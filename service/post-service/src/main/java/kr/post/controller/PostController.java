package kr.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.post.component.PostModel;
import kr.post.entity.PostEntity;
import kr.post.service.ImageService;
import kr.post.service.PostService;
import kr.post.service.UpvoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/posts")
public class PostController {
    private final PostService service;
    private final UpvoteService upvoteService;
    private final ImageService imageService;

    @GetMapping("/{restaurantId}/allAverage")
    public ResponseEntity<Double> getAllAverageRating(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(service.allAverageRating(restaurantId));
    }

    // 좋아요 관련 : like, unlike, hasLiked, getLikeCount
    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> like(@PathVariable Long postId, @RequestParam String userId) {
        return ResponseEntity.ok(upvoteService.like(postId, userId));
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<Boolean> unlike(@PathVariable Long postId, @RequestParam String userId) {
        return ResponseEntity.ok(upvoteService.unlike(postId, userId));
    }

    @GetMapping("/{postId}/hasLiked")
    public ResponseEntity<Boolean> hasLiked(@PathVariable Long postId, @RequestParam String userId) {
        return ResponseEntity.ok(upvoteService.hasLiked(postId, userId));
    }

    @GetMapping("/{postId}/like-count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(upvoteService.getLikeCount(postId));
    }

    @GetMapping("/crawling")
    public ResponseEntity<Boolean> crawl() {
        return ResponseEntity.ok(service.crawling());
    }

    @GetMapping("/group/{page}")
    public ResponseEntity<List<PostModel>> getPostPage(@PathVariable int page) {
        return ResponseEntity.ok(service.findAllPerPage(page));
    }

    @GetMapping("/{restaurantId}/group")
    public ResponseEntity<List<PostModel>> getListByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(service.findAllByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostModel> getPostId(@PathVariable Long id) {
        PostModel postModel = service.postWithImage(id);
        return ResponseEntity.ok(postModel);
    }

    @GetMapping("/exist/{id}")
    public ResponseEntity<Boolean> existsPostById(@PathVariable Long id) {
        return ResponseEntity.ok(service.existsById(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countPost() {
        return ResponseEntity.ok(service.count());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletePostById(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

    @PostMapping("")
    public ResponseEntity<Long> createPostWithImg(@RequestPart("model") PostModel model, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Long postId = service.createPostWithImages(model);
        if (files != null && !files.isEmpty()) {
            PostEntity postEntity = service.findEntityById(postId);
            imageService.uploadFiles(files, postEntity);
        }
        return ResponseEntity.ok(postId);
    }

    @PutMapping("")
    public ResponseEntity<Long> updatePost(@RequestPart("postData") String postData,
                                           @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles,
                                           @RequestPart(value = "imagesToDelete", required = false) List<Long> imagesToDelete) {

        ObjectMapper objectMapper = new ObjectMapper();
        PostModel postModel;
        try {
            postModel = objectMapper.readValue(postData, PostModel.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }

        Long postId = service.updatePost(postModel);
        imageService.updateImages(postId, multipartFiles, imagesToDelete);
        return ResponseEntity.ok(postId);

    }

    @GetMapping("/list/{id}")
    public ResponseEntity<List<?>> userPostList(@PathVariable String id) {
        System.out.println("PostController.userPostList");
        return ResponseEntity.ok(service.findByUserId(id));
    }
}

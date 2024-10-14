package kr.post.controller;

import kr.post.component.ReplyModel;
import kr.post.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyController {
    private final ReplyService service;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReplyModel>> getReplyByPostId(@PathVariable Long postId){
        return ResponseEntity.ok(service.findAllByPostId(postId));
    }

    @GetMapping("/group")
    public ResponseEntity<List<ReplyModel>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReplyModel> getReplyById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/exist/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(service.existsById(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReplyModel> update(@PathVariable Long id, @RequestBody ReplyModel model) {
      return ResponseEntity.ok(service.update(id, model));
    }

    @PostMapping("")
    public ResponseEntity<ReplyModel> insertReply(@RequestBody ReplyModel model) {
        return ResponseEntity.ok(service.save(model));
    }
}

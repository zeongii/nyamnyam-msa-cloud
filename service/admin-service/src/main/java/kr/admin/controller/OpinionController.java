package kr.admin.controller;


import kr.admin.component.OpinionModel;
import kr.admin.entity.OpinionEntity;
import kr.admin.service.OpinionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opinion")
@RequiredArgsConstructor
@CrossOrigin
public class OpinionController {

    private final OpinionService opinionService;

    @GetMapping("")
    public ResponseEntity<List<?>> findAll() {
        return ResponseEntity.ok(opinionService.findAll());

    }

   @GetMapping("/{id}")
    public ResponseEntity<OpinionEntity> findById(@PathVariable Long id) {
        return ResponseEntity.ok(opinionService.findById(id));
    }


   @PostMapping("")
    public ResponseEntity<OpinionEntity> save(@RequestBody OpinionModel model) {
        return ResponseEntity.ok(opinionService.save(model));
    }
}

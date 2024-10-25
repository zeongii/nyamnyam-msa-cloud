package kr.admin.controller;

import kr.admin.absent.chart.CostModel;

import kr.admin.entity.RestaurantEntity;
import kr.admin.service.AdminService;
import kr.admin.service.OpinionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {


    private final AdminService adminService;
    private final OpinionService opinionService;

    @GetMapping("/countUserList")
    public ResponseEntity<List<?>> countUserList() {
        return ResponseEntity.ok(adminService.countUserList());
    }

    @GetMapping("/countAreaList")
    public ResponseEntity<List<?>> countAreaList() {
        return ResponseEntity.ok(adminService.countAreaList());
    }

    @GetMapping("/countPostList")
    public ResponseEntity<List<?>> countPostList() {
        return ResponseEntity.ok(adminService.countPostList());
    }

    @GetMapping("/typeList/{id}")
    public ResponseEntity<List<?>> typeList(@PathVariable String id) {
        return ResponseEntity.ok(adminService.typeList(id));
    }

    @GetMapping("/randomByUserId/{id}")
    public ResponseEntity<RestaurantEntity> randomByUserId(@PathVariable String id) {
        return ResponseEntity.ok(adminService.randomRestaurantByUserId(id));
    }

    @GetMapping("/receiptCount")
    public ResponseEntity<List<CostModel>> receiptCount() {
        return ResponseEntity.ok(adminService.receiptRestaurant());
    }

    @GetMapping("/upvoteRestaurant")
    public ResponseEntity<List<?>> upvoteRestaurant() {
        return ResponseEntity.ok(adminService.findRestaurantFromUpvote());
    }

    @GetMapping("userAreaList/{id}")
    public ResponseEntity<List<?>> userAreaList(@PathVariable String id) {
        return ResponseEntity.ok(adminService.userAreaList(id));
    }

    @GetMapping("/todayPost")
    public ResponseEntity<List<?>> todayList() {
        return ResponseEntity.ok(adminService.findPostsByToday());
    }

}

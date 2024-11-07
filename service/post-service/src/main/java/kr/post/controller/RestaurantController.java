package kr.post.controller;

import kr.post.component.RestaurantModel;
import kr.post.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 레스토랑 목록 불러오는 api
    @GetMapping("/restaurants")
    public List<RestaurantModel> getRestaurants() {
        return restaurantService.findAll();
    }

    // 맛집 검색(이름, 유형, 메뉴)
    @GetMapping("/search")
    public List<RestaurantModel> searchRestaurants(@RequestParam("q") String query) {
        return restaurantService.searchRestaurants(query);
    }

    @GetMapping("/tag")
    public List<RestaurantModel> getRestaurantsByTag(@RequestParam("name")  List<String> tagNames) {
        return restaurantService.getRestaurantsByTag(tagNames);
    }

    // 맛집 상세보기
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantModel> getRestaurant(@PathVariable Long id) {
        ResponseEntity<RestaurantModel> restaurantOpt = restaurantService.getOneRestaurant(id);
        return restaurantOpt;
    }

    @GetMapping("/category")
    public List<RestaurantModel> getRestaurantsByCategory(@RequestParam("category") List<String> category) {
        return restaurantService.findByCategory(category);
    }




}

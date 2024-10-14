package kr.restaurant.controller;



import kr.restaurant.component.RestaurantModel;
import kr.restaurant.component.WishListModel;
import kr.restaurant.entity.WishListEntity;
import kr.restaurant.entity.WishListRestaurantEntity;
import kr.restaurant.service.WishListRestaurantService;
import kr.restaurant.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/wishList")
public class WishListController {
    private final WishListService wishListService;
    private final WishListRestaurantService wishListRestaurantService;


    // 위시리스트 항목 새로 추가
    @PostMapping
    public ResponseEntity<WishListEntity> createWishList(@RequestHeader String userId, @RequestParam String name) {
        WishListEntity wishList = wishListService.createWishList(name, userId);
        return ResponseEntity.ok(wishList);
    }

    // 추가된 항목에 식당 추가
    @PostMapping("/{wishListId}")
    public ResponseEntity<WishListRestaurantEntity> addRestaurantToWishList(@RequestHeader String userId, @PathVariable Long wishListId, @RequestParam Long restaurantId) {
        WishListRestaurantEntity wishListRestaurantEntity = wishListRestaurantService.addRestaurantToWishList(userId, wishListId, restaurantId);
        return ResponseEntity.ok(wishListRestaurantEntity);
    }

    // 전체 항목 목록 가져오기
    @GetMapping
    public List<WishListModel> getWishLists(@RequestHeader String userId) {
        return wishListService.getWishLists(userId);
    }

/*    @GetMapping("/{wishListId}/restaurants")
    public ResponseEntity<List<RestaurantModel>> getRestaurants(
            @RequestHeader Long userId,
            @PathVariable Long wishListId) {
        List<RestaurantModel> restaurants = wishListRestaurantService.findRestaurantsByUserIdAndWishListId(userId, wishListId);
        return ResponseEntity.ok(restaurants);
    }*/

    // 선택한 위시리스트에 들어있는 식당들 가져오기
    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantModel>> getRestaurants(
            @RequestHeader String userId,
            @RequestParam Long wishListId) {
        List<RestaurantModel> restaurants = wishListRestaurantService.findRestaurantsByUserIdAndWishListId(userId, wishListId);
        return ResponseEntity.ok(restaurants);
    }


    // 식당 위시리스트에서 해제시키기
    @DeleteMapping
    public ResponseEntity<Boolean> deleteWishList(@RequestHeader String userId , @RequestParam Long restaurantId) {
        boolean deleted = wishListRestaurantService.deleteRestaurantFromWishList(userId, restaurantId);
        return ResponseEntity.ok(deleted);
    }

    // 해당 유저가 위시리스트에(항목 상관없이) 추가한 식당 전체(하트 상태 유지 위해)
    @GetMapping("/getAll")
    public List<Long> getRestaurants(@RequestHeader String userId) {
        return wishListRestaurantService.getDistinctRestaurantsByUserId(userId);
    }

    // 위시 항목 삭제하기
    @DeleteMapping("/delete/wishList")
    public ResponseEntity<Boolean> deleteWish(@RequestHeader String userId, @RequestParam Long id) {
        boolean deleted = wishListService.deleteWishList(userId, id);
        return ResponseEntity.ok(deleted);
    }

}

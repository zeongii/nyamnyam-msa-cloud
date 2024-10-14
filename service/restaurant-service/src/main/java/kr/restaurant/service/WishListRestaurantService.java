package kr.restaurant.service;


import kr.restaurant.component.RestaurantModel;
import kr.restaurant.entity.WishListRestaurantEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WishListRestaurantService {

    WishListRestaurantEntity addRestaurantToWishList(String userId, Long wishListId, Long restaurantId);

    List<RestaurantModel> findRestaurantsByUserIdAndWishListId(String userId, Long wishListId);

    @Transactional
    boolean deleteRestaurantFromWishList(String userId, Long restaurantId);

    List<Long> getDistinctRestaurantsByUserId(String userId);
}

package kr.restaurant.repositoryCustom;


import kr.restaurant.entity.RestaurantEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WishListRestaurantCustomRepository {

    List<RestaurantEntity> findRestaurantsByUserIdAndWishListId(String userId, Long wishListId);


    @Transactional
    boolean deleteRestaurantFromWishList(String userId, Long restaurantId);

    List<Long> getDistinctRestaurantIdsByUserId(String userId);
}

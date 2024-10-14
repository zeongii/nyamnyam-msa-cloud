package kr.restaurant.serviceImpl;



import kr.restaurant.component.RestaurantModel;
import kr.restaurant.entity.RestaurantEntity;
import kr.restaurant.entity.WishListRestaurantEntity;
import kr.restaurant.repository.RestaurantRepository;
import kr.restaurant.repository.WishListRepository;
import kr.restaurant.repository.WishListRestaurantRepository;
import kr.restaurant.service.WishListRestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishListRestaurantServiceImpl implements WishListRestaurantService {
    private final WishListRestaurantRepository wishListRestaurantRepository;
    private final WishListRepository wishListRepository;
    private final RestaurantRepository restaurantRepository;


    @Override
    public WishListRestaurantEntity addRestaurantToWishList(String userId, Long wishListId, Long restaurantId) {


        if (!wishListRepository.existsByIdAndUserId(wishListId, userId)) {
            return null;
        }
        if (wishListRestaurantRepository.existsByWishListIdAndRestaurantId(wishListId, restaurantId)) {
            return null;
        }

        WishListRestaurantEntity wishListRestaurant = WishListRestaurantEntity.builder()
                .wishListId(wishListId)
                .restaurantId(restaurantId)
                .userId(userId)
                .build();


        return wishListRestaurantRepository.save(wishListRestaurant);
    }

    @Override
    public List<RestaurantModel> findRestaurantsByUserIdAndWishListId(String userId, Long wishListId) {
        List<RestaurantEntity> restaurants = wishListRestaurantRepository.findRestaurantsByUserIdAndWishListId(userId, wishListId);
        return restaurants.stream()
                .map(RestaurantModel::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public boolean deleteRestaurantFromWishList(String userId, Long restaurantId) {
        boolean exists  = wishListRestaurantRepository.deleteRestaurantFromWishList(userId, restaurantId);
        if (!exists) {
            return false; // 레코드가 존재하지 않으면 false 반환
        }
       // boolean deletedCount = wishListRestaurantRepository.deleteRestaurantFromWishList(userId, restaurantId);
        return true; // 삭제된 행이 있으면 true 반환
    }

    @Override
    public List<Long> getDistinctRestaurantsByUserId(String userId) {
        return wishListRestaurantRepository.getDistinctRestaurantIdsByUserId(userId);
    }




}

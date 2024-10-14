package kr.restaurant.repository;



import kr.restaurant.entity.WishListRestaurantEntity;
import kr.restaurant.repositoryCustom.WishListRestaurantCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRestaurantRepository extends JpaRepository<WishListRestaurantEntity, Long> , WishListRestaurantCustomRepository {

    boolean existsByWishListIdAndRestaurantId(Long wishListId, Long restaurantId);


}

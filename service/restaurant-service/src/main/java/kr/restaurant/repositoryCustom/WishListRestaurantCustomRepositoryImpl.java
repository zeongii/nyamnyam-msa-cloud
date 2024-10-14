package kr.restaurant.repositoryCustom;


import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.restaurant.entity.QRestaurantEntity;
import kr.restaurant.entity.QWishListRestaurantEntity;
import kr.restaurant.entity.RestaurantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class WishListRestaurantCustomRepositoryImpl implements WishListRestaurantCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RestaurantEntity> findRestaurantsByUserIdAndWishListId(String userId, Long wishListId) {
        QWishListRestaurantEntity qWishListRestaurantEntity = QWishListRestaurantEntity.wishListRestaurantEntity;
        QRestaurantEntity qRestaurantEntity = QRestaurantEntity.restaurantEntity;

        return jpaQueryFactory.selectFrom(qRestaurantEntity)
                .innerJoin(qWishListRestaurantEntity)
                .on(qRestaurantEntity.id.eq(qWishListRestaurantEntity.restaurantId))
                .where(qWishListRestaurantEntity.userId.eq(userId)
                        .and(qWishListRestaurantEntity.wishListId.eq(wishListId)))
                .fetch();
    }

    @Transactional
    @Override
    public boolean deleteRestaurantFromWishList(String userId, Long restaurantId) {
        QWishListRestaurantEntity wishListRestaurant = QWishListRestaurantEntity.wishListRestaurantEntity;

        long deletedCount = jpaQueryFactory.delete(wishListRestaurant)
                .where(wishListRestaurant.userId.eq(userId)
                        .and(wishListRestaurant.restaurantId.eq(restaurantId)))
                .execute();

        return deletedCount > 0;
    }

    @Override
    public List<Long> getDistinctRestaurantIdsByUserId(String userId) {
        QWishListRestaurantEntity wishListRestaurant = QWishListRestaurantEntity.wishListRestaurantEntity;

        return jpaQueryFactory
                .select(wishListRestaurant.restaurantId)
                .distinct()
                .from(wishListRestaurant)
                .where(wishListRestaurant.userId.eq(userId))
                .orderBy(wishListRestaurant.restaurantId.asc())
                .fetch();
    }

}

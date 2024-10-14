package kr.restaurant.repositoryCustom;


import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.restaurant.entity.QWishListEntity;
import kr.restaurant.entity.QWishListRestaurantEntity;
import kr.restaurant.entity.WishListEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class WishListCustomRepositoryImpl implements WishListCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<WishListEntity> getWishLists(String userId) {
        QWishListEntity qWishListEntity = QWishListEntity.wishListEntity;

        return jpaQueryFactory.selectFrom(qWishListEntity)
                .distinct()
                .where(qWishListEntity.userId.eq(userId))
                .fetch();
    }

    public boolean deleteWishList(String userId, Long id) {
        QWishListEntity wishList = QWishListEntity.wishListEntity;
        QWishListRestaurantEntity wishListRestaurants = QWishListRestaurantEntity.wishListRestaurantEntity;

        // 위시리스트 삭제할때 안의 식당들도 삭제

        jpaQueryFactory.delete(wishListRestaurants)
                .where(wishListRestaurants.wishListId.eq(
                        JPAExpressions.select(wishList.id)
                                .from(wishList)
                                .where(wishList.userId.eq(userId).and(wishList.id.eq(id)))
                )).execute();


        jpaQueryFactory.delete(wishList)
                .where(wishList.userId.eq(userId).and(wishList.id.eq(id)))
                .execute();

        return true;
    }



}

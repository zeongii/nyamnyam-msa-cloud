package kr.restaurant.repositoryCustom;



import kr.restaurant.entity.WishListEntity;

import java.util.List;

public interface WishListCustomRepository {

    List<WishListEntity> getWishLists(String userId);

    boolean deleteWishList(String userId, Long id);
}

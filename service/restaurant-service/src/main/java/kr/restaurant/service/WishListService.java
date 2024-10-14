package kr.restaurant.service;


import kr.restaurant.component.WishListModel;
import kr.restaurant.entity.WishListEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface WishListService {


    WishListEntity createWishList(String name, String userId);

    List<WishListModel> getWishLists(String userId);


    @Transactional
    boolean deleteWishList(String userId, Long wishListId);
}

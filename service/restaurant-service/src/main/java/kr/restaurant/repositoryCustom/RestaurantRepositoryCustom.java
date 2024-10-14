package kr.restaurant.repositoryCustom;


import kr.restaurant.absent.Chart.AreaModel;
import kr.restaurant.entity.RestaurantEntity;

import java.util.List;

public interface RestaurantRepositoryCustom {

    List<RestaurantEntity> searchRestaurant(String keyword);

    List<RestaurantEntity> findByTagName(List<String> tagNames);

    List<RestaurantEntity> findByCategoryUsingMenu(List<String> categories);

    List<AreaModel> countAreaList();

    List<AreaModel> userAreaList(String userId);

    RestaurantEntity randomRestaurant(String userId);

}
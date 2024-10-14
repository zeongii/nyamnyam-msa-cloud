package kr.post.repositoryCustom;

import java.util.List;

import kr.post.absent.Chart.AreaModel;
import kr.post.entity.RestaurantEntity;

public interface RestaurantRepositoryCustom {

    List<RestaurantEntity> searchRestaurant(String keyword);

    List<RestaurantEntity> findByTagName(List<String> tagNames);

    List<RestaurantEntity> findByCategoryUsingMenu(List<String> categories);

    List<AreaModel> countAreaList();

    List<AreaModel> userAreaList(String userId);

    RestaurantEntity randomRestaurant(String userId);

}
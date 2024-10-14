package kr.post.service;

import kr.post.component.RestaurantModel;
import kr.post.entity.RestaurantEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


public interface RestaurantService {


    List<RestaurantEntity> getCrawlingInfos();
    List<RestaurantModel> findAll();
    List<RestaurantModel> searchRestaurants(String keyword);
    RestaurantModel findById(Long id);
    Boolean existsById(Long id);
    ResponseEntity<RestaurantModel> getOneRestaurant(@PathVariable Long id);
    List<RestaurantModel> getRestaurantsByTag(List<String> tagNames);
    List<RestaurantModel> findByCategory(List<String> categories);


}

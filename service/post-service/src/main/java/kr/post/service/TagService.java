package kr.post.service;

import kr.post.component.TagModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TagService {

    Map<String, List<TagModel>> getTagsByCategory();

    List<TagModel> findAll();

    Boolean save(TagModel model);

    Optional<TagModel> findByName(String name);

    Boolean existsByName(String name);

    Boolean deleteByName(String name);

    // 레스토랑 별 Top5 Tag
    List<String> getTagRestaurant(Long restaurantId);

    List<String> getTagCategory();

}

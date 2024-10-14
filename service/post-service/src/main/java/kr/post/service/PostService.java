package kr.post.service;

import kr.post.absent.Chart.UserPostModel;
import kr.post.component.PostModel;
import kr.post.entity.PostEntity;

import java.util.List;

public interface PostService {

    double allAverageRating(Long restaurantId);

    PostModel postWithImage(Long id);

    PostEntity findEntityById(Long id);

    List<PostModel> findAllByRestaurant(Long restaurantId);

    PostModel findById(Long id);

    Boolean existsById(Long id);

    Long count();

    Boolean deleteById(Long id);

    List<PostModel> findAllPerPage(int page);

    Boolean crawling();

    Long createPost(PostModel model);

    Long updatePost(PostModel model);

    List<UserPostModel> findByUserId(String userId);

    Long createPostWithImages(PostModel model);
}


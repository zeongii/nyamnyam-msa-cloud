package kr.restaurant.repositoryCustom;

import com.querydsl.core.Tuple;
import kr.restaurant.absent.Chart.CountModel;
import kr.restaurant.absent.Chart.TotalModel;
import kr.restaurant.absent.Chart.UserPostModel;
import kr.restaurant.entity.PostEntity;


import java.util.List;

public interface PostRepositoryCustom {

    List<PostEntity> findAllByRestaurantWithNickname(Long restaurantId);

    // 단일 post에 nickname 불러오기
    Tuple findPostWithNicknameById(Long postId);

    List<CountModel> findNicknamesWithCounts();

    List<String> findNicknameFromUpvote();

    List<TotalModel> findRestaurantFromUpvote();

    List<TotalModel> countRestaurantList();



    List<CountModel> typeList(String userId);



}

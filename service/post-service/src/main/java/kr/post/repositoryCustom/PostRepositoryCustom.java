package kr.post.repositoryCustom;

import com.querydsl.core.Tuple;
import kr.post.absent.Chart.CountModel;
import kr.post.absent.Chart.TotalModel;
import kr.post.absent.Chart.UserPostModel;
import kr.post.entity.PostEntity;

import java.util.List;

public interface PostRepositoryCustom {

    List<PostEntity> findAllByRestaurantWithNickname(Long restaurantId);

    // 단일 post에 nickname 불러오기
    Tuple findPostWithNicknameById(Long postId);

    List<CountModel> findNicknamesWithCounts();

    List<String> findNicknameFromUpvote();

    List<TotalModel> findRestaurantFromUpvote();

    List<TotalModel> countRestaurantList();

    List<UserPostModel> findByUserId(String userId);

    List<CountModel> typeList(String userId);



}

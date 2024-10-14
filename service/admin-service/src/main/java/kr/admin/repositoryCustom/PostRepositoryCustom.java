package kr.admin.repositoryCustom;

import com.querydsl.core.Tuple;
import kr.admin.absent.chart.CountModel;
import kr.admin.absent.chart.TotalModel;
import kr.admin.absent.chart.UserPostModel;
import kr.admin.entity.PostEntity;


import java.util.List;

public interface PostRepositoryCustom {


    List<CountModel> findNicknamesWithCounts();


    List<TotalModel> findRestaurantFromUpvote();

    List<TotalModel> countRestaurantList();

    List<CountModel> typeList(String userId);



}

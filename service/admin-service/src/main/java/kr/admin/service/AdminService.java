package kr.admin.service;



import kr.admin.absent.chart.AreaModel;
import kr.admin.absent.chart.CostModel;
import kr.admin.absent.chart.CountModel;
import kr.admin.absent.chart.TotalModel;
import kr.admin.component.PostModel;
import kr.admin.entity.RestaurantEntity;

import java.util.List;

public interface AdminService {

    List<CountModel> countUserList();

    List<TotalModel> findRestaurantFromUpvote();

    List<AreaModel> countAreaList();

    List<TotalModel> countPostList();

    RestaurantEntity randomRestaurantByUserId(String userId);

    List<CostModel> receiptRestaurant();

    List<CountModel> typeList(String userId);

    List<AreaModel> userAreaList(String id);

    List<PostModel> findPostsByToday();
}

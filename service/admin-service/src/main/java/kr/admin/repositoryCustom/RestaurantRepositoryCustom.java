package kr.admin.repositoryCustom;




import kr.admin.absent.chart.AreaModel;
import kr.admin.entity.RestaurantEntity;

import java.util.List;

public interface RestaurantRepositoryCustom {

    List<AreaModel> countAreaList();

    List<AreaModel> userAreaList(String userId);

    RestaurantEntity randomRestaurant(String userId);

}
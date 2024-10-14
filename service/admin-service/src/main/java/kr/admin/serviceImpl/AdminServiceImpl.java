package kr.admin.serviceImpl;


import kr.admin.absent.chart.AreaModel;
import kr.admin.absent.chart.CostModel;
import kr.admin.absent.chart.CountModel;
import kr.admin.absent.chart.TotalModel;
import kr.admin.component.PostModel;
import kr.admin.entity.PostEntity;
import kr.admin.entity.RestaurantEntity;
import kr.admin.repository.PostRepository;
import kr.admin.repository.ReceiptRepository;
import kr.admin.repository.RestaurantRepository;
import kr.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PostRepository postRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReceiptRepository receiptRepository;
    private final PostServiceImpl postServiceImpl;

    @Override
    public List<CountModel> countUserList() {
        return postRepository.findNicknamesWithCounts();
    }


    // 제일 좋아요를 많이 받은 음식점
    @Override
    public List<TotalModel> findRestaurantFromUpvote() {
        List<TotalModel> restaurantFromUpvote = postRepository.findRestaurantFromUpvote();
        System.out.println(restaurantFromUpvote);


        return postRepository.findRestaurantFromUpvote();
    }

    // 레스토랑이 많이 등록된 지역 list
    @Override
    public List<AreaModel> countAreaList() {
        return restaurantRepository.countAreaList();
    }


    // post가 많은 레스토랑
    @Override
    public List<TotalModel> countPostList() {
        return postRepository.countRestaurantList();
    }


    @Override
    public RestaurantEntity randomRestaurantByUserId(String userId) {
        return restaurantRepository.randomRestaurant(userId);
    }

    @Override
    public List<CostModel> receiptRestaurant() {
        return receiptRepository.receiptCount();
    }

    @Override
    public List<CountModel> typeList(String userId) {
        return postRepository.typeList(userId);
    }

    @Override
    public List<AreaModel> userAreaList(String id) {
        return restaurantRepository.userAreaList(id);
    }

    @Override
    public List<PostModel> findPostsByToday() {
        List<PostEntity> postsByToday = postRepository.findPostsByToday();

        return postsByToday.stream()
                .map(postServiceImpl::convertToModel)
                .collect(Collectors.toList());
    }


}

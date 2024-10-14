package kr.admin.repositoryCustom;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.admin.absent.chart.CountModel;
import kr.admin.absent.chart.TotalModel;
import kr.admin.absent.chart.UserPostModel;
import kr.admin.entity.PostEntity;
import kr.admin.entity.QPostEntity;
import kr.admin.entity.QRestaurantEntity;
import kr.admin.entity.QUpvoteEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    // 가장 많은 post 쓴 사람 순위 뽑아내기
    @Override
    public List<CountModel> findNicknamesWithCounts() {
        QPostEntity postEntity = QPostEntity.postEntity;

        JPAQuery<Tuple> query = jpaQueryFactory
                .select(postEntity.nickname, postEntity.count())
                .from(postEntity)
                .groupBy(postEntity.nickname)
                .orderBy(postEntity.count().desc());

        List<Tuple> result = query.fetch();


        return result.stream()
                .map(tuple -> new CountModel(
                        tuple.get(postEntity.nickname),
                        tuple.get(postEntity.count())))
                .collect(Collectors.toList());
    }


    // 가장 많은 추천을 받은 음식점 list
    @Override
    public List<TotalModel> findRestaurantFromUpvote() {

        QPostEntity postEntity = QPostEntity.postEntity;
        QUpvoteEntity upvoteEntity = QUpvoteEntity.upvoteEntity;
        QRestaurantEntity restaurantEntity = QRestaurantEntity.restaurantEntity;

        List<Tuple> results =  jpaQueryFactory.select(restaurantEntity.name,upvoteEntity.postId.count() )
                .from(upvoteEntity)
                .join(postEntity).on(postEntity.id.eq(upvoteEntity.postId))
                .join(restaurantEntity).on(restaurantEntity.id.eq(postEntity.id))
                .groupBy(upvoteEntity.postId)
                .orderBy(upvoteEntity.postId.asc())
                .limit(5)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    TotalModel totalModel = new TotalModel();
                    totalModel.setRestaurantName(tuple.get(restaurantEntity.name));
                    totalModel.setTotal(tuple.get(upvoteEntity.postId.count()));
                    return totalModel;
                })
                .collect(Collectors.toList());



    }


    // 포스트가 가장 많은 음식점 list
    @Override
    public List<TotalModel> countRestaurantList() {
        QPostEntity postEntity = QPostEntity.postEntity;
        QRestaurantEntity restaurantEntity = QRestaurantEntity.restaurantEntity;

        List<Tuple> results = jpaQueryFactory.select(
                        restaurantEntity.name,
                        postEntity.restaurant.id.count()
                )
                .from(restaurantEntity)
                .leftJoin(postEntity)
                .on(restaurantEntity.id.eq(postEntity.restaurant.id))
                .groupBy(restaurantEntity.name)
                .orderBy(postEntity.restaurant.id.count().desc())
                .limit(5)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    TotalModel totalModel = new TotalModel();
                    totalModel.setRestaurantName(tuple.get(restaurantEntity.name));
                    totalModel.setTotal(tuple.get(postEntity.restaurant.id.count()));
                    return totalModel;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<CountModel> typeList(String userId) {
        QRestaurantEntity restaurant = QRestaurantEntity.restaurantEntity;
        QPostEntity post = QPostEntity.postEntity;

        List<Tuple> results = jpaQueryFactory
                .select(restaurant.type, restaurant.type.count())
                .from(post)
                .join(restaurant).on(restaurant.id.eq(post.restaurant.id))
                .where(post.userId.eq(userId))
                .groupBy(restaurant.type)
                .limit(5)
                .fetch();

        return results.stream()
                .map(tuple -> new CountModel(
                        tuple.get(restaurant.type),
                        tuple.get(restaurant.type.count())
                ))
                .collect(Collectors.toList());

    }






}

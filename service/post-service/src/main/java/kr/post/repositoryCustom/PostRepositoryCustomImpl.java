package kr.post.repositoryCustom;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.post.absent.Chart.CountModel;
import kr.post.absent.Chart.TotalModel;
import kr.post.absent.Chart.UserPostModel;
import kr.post.entity.PostEntity;
import kr.post.entity.QPostEntity;
import kr.post.entity.QRestaurantEntity;
import kr.post.entity.QUpvoteEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    // 레스토랑ID 기반으로 여러 개의 post에 nickname 불러오기
    @Override
    public List<PostEntity> findAllByRestaurantWithNickname(Long restaurantId) {
        QPostEntity postEntity = QPostEntity.postEntity;

        return jpaQueryFactory
                .select(postEntity)
                .from(postEntity)
                .where(postEntity.restaurant.id.eq(restaurantId))
                .fetch();
    }
    // 단일 post에 nickname 불러오기
    @Override
    public Tuple findPostWithNicknameById(Long postId){
        QPostEntity postEntity = QPostEntity.postEntity;

        return jpaQueryFactory
                .select(postEntity, postEntity.nickname)
                .from(postEntity)
                .where(postEntity.id.eq(postId))
                .fetchOne();
    }

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

    @Override
    public List<String> findNicknameFromUpvote() {
        return List.of();
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
    public List<UserPostModel> findByUserId(String userId) {
        QPostEntity post = QPostEntity.postEntity;
        QRestaurantEntity restaurant = QRestaurantEntity.restaurantEntity;
        QUpvoteEntity upvote = QUpvoteEntity.upvoteEntity;

        List<Tuple> results = jpaQueryFactory
                .select(restaurant.name, post.content, post.modifyDate, post.id,restaurant.id)
                .from(post)
                .join(restaurant).on(restaurant.id.eq(post.restaurant.id))
                .where(post.userId.eq(userId))
                .fetch();

        List<Tuple> result = jpaQueryFactory
                .select(post.id, upvote.postId.count())
                .from(post)
                .leftJoin(upvote).on(upvote.postId.eq(post.id))
                .groupBy(post.id)
                .fetch();

        Map<Long, Long> upvoteCountMap = result.stream()
                .collect(Collectors.toMap(tuple -> tuple.get(post.id), tuple -> tuple.get(upvote.postId.count())));


        return results.stream()
                .map(tuple -> {
                    UserPostModel userPostModel = new UserPostModel();
                    userPostModel.setName(tuple.get(restaurant.name));
                    userPostModel.setContent(tuple.get(post.content));
                    userPostModel.setEntryDate(tuple.get(post.modifyDate));
                    userPostModel.setRestaurantId(tuple.get(restaurant.id));
                    Long postId = tuple.get(post.id);
                    Long upvoteCount = upvoteCountMap.get(postId);
                    userPostModel.setUpvoteCount(upvoteCount);
                    userPostModel.setPostId(postId);
                    return userPostModel;
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

package kr.admin.repositoryCustom;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.admin.absent.chart.AreaModel;
import kr.admin.entity.QPostEntity;
import kr.admin.entity.QRestaurantEntity;
import kr.admin.entity.RestaurantEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;



    @Override
    public List<AreaModel> countAreaList() {
        QRestaurantEntity restaurantEntity = QRestaurantEntity.restaurantEntity;

        List<Tuple> results = jpaQueryFactory.select(
                        Expressions.stringTemplate("regexp_substr({0}, '([^ ]+구)', 1, 1)", restaurantEntity.address).as("district"),
                        restaurantEntity.address.count()
                )
                .from(restaurantEntity)
                .groupBy(Expressions.stringTemplate("regexp_substr({0}, '([^ ]+구)', 1, 1)", restaurantEntity.address))
                .orderBy(restaurantEntity.address.count().desc())
                .limit(5)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    AreaModel areaModel = new AreaModel();
                    areaModel.setArea(tuple.get(0, String.class));
                    areaModel.setTotal(tuple.get(1, Long.class));
                    return areaModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AreaModel> userAreaList(String userId) {
        QRestaurantEntity restaurantEntity = QRestaurantEntity.restaurantEntity;
        QPostEntity postEntity = QPostEntity.postEntity;

        List<Tuple> results = jpaQueryFactory.select(
                        Expressions.stringTemplate("regexp_substr({0}, '([^ ]+구)', 1, 1)", restaurantEntity.address).as("district"),
                        restaurantEntity.address.count()
                )
                .from(restaurantEntity)
                .join(postEntity).on(postEntity.restaurant.id.eq(restaurantEntity.id))
                .where(postEntity.userId.eq(userId))
                .groupBy(Expressions.stringTemplate("regexp_substr({0}, '([^ ]+구)', 1, 1)", restaurantEntity.address))
                .orderBy(restaurantEntity.address.count().desc())
                .limit(5)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    AreaModel areaModel = new AreaModel();
                    areaModel.setArea(tuple.get(0, String.class));
                    areaModel.setTotal(tuple.get(1, Long.class));
                    return areaModel;
                })
                .collect(Collectors.toList());
    }




    // 랜덤레스토랑
    @Override
    public RestaurantEntity randomRestaurant(String userId) {
        QRestaurantEntity restaurant = QRestaurantEntity.restaurantEntity;
        QPostEntity post = QPostEntity.postEntity;

        String type = jpaQueryFactory
                .select(restaurant.type)
                .from(post)
                .join(restaurant).on(restaurant.id.eq(post.restaurant.id))
                .where(post.userId.eq(userId))
                .groupBy(restaurant.type)
                .orderBy(post.count().desc())
                .limit(1)
                .fetchOne();

        RestaurantEntity randomRestaurant = jpaQueryFactory
                .selectFrom(restaurant)
                .where(restaurant.type.eq(type))
                .orderBy(numberTemplate(Double.class, "function('RAND')").asc())
                .limit(1)
                .fetchOne();

        return randomRestaurant;

    }

}
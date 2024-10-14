package kr.admin.repositoryCustom;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.admin.absent.chart.CostModel;
import kr.admin.entity.QReceiptEntity;
import kr.admin.entity.QRestaurantEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;


@RequiredArgsConstructor
public class ReceiptRepositoryCustomImpl implements ReceiptRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public Long findRestaurantId(String name) {
        QReceiptEntity receiptEntity = QReceiptEntity.receiptEntity;
        QRestaurantEntity restaurantEntity = QRestaurantEntity.restaurantEntity;
        System.out.println(name);

        return jpaQueryFactory.select(restaurantEntity.id)
                .from(receiptEntity)
                .join(restaurantEntity).on(restaurantEntity.name.eq(receiptEntity.name))
                .where(restaurantEntity.name.eq(name))
                .fetchFirst();
    }



    @Override
    public List<CostModel> costList(String userId) {
        QReceiptEntity receiptEntity = QReceiptEntity.receiptEntity;

        List<Tuple> results = jpaQueryFactory
                .select(
                        stringTemplate("DATE_FORMAT({0}, {1})", receiptEntity.date, "%Y-%m"),
                        receiptEntity.price.sum()

                )
                .from(receiptEntity)
                .where(receiptEntity.userId.eq(userId))
                .groupBy(stringTemplate("DATE_FORMAT({0}, {1})", receiptEntity.date, "%Y-%m"))
                .fetch();

        return results.stream()
                .map(tuple -> {
                    CostModel costModel = new CostModel();
                    costModel.setDate(tuple.get(0, String.class));
                    costModel.setPrice(tuple.get(1, Long.class));
                    return costModel;
                })
                .collect(Collectors.toList());

    }

    @Override
    public List<CostModel> receiptCount() {
        QReceiptEntity receiptEntity = QReceiptEntity.receiptEntity;

        List<Tuple> result = jpaQueryFactory
                .select(
                        stringTemplate("DATE_FORMAT({0}, {1})", receiptEntity.entryDate, "%Y-%m").as("month"),
                        receiptEntity.count().as("count")
                )
                .from(receiptEntity)
                .groupBy(stringTemplate("DATE_FORMAT({0}, {1})", receiptEntity.entryDate, "%Y-%m"))
                .orderBy(stringTemplate("DATE_FORMAT({0}, {1})", receiptEntity.entryDate, "%Y-%m").asc())
                .fetch();

        return result.stream()
                .map(tuple -> {
                    CostModel costModel = new CostModel();
                    costModel.setDate(tuple.get(0, String.class));
                    costModel.setPrice(tuple.get(1, Long.class));
                    return costModel;
                })
                .collect(Collectors.toList());

    }




}

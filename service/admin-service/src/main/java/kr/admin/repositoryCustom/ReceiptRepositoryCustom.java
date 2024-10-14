package kr.admin.repositoryCustom;


import kr.admin.absent.chart.CostModel;

import java.util.List;

public interface ReceiptRepositoryCustom {

    Long findRestaurantId(String name);

    List<CostModel> costList(String userId);

    List<CostModel> receiptCount();

}

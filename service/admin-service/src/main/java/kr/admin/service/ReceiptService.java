package kr.admin.service;

import kr.admin.absent.chart.CostModel;
import kr.admin.component.RestaurantModel;
import kr.admin.entity.ReceiptEntity;

import java.util.List;

public interface ReceiptService {

    RestaurantModel save(ReceiptEntity receipt);

    List<CostModel> costModelList(String userId);

    List<ReceiptEntity> findByUserId(String id);

    Boolean deleteById(Long id);

    ReceiptEntity findById(Long id);

    ReceiptEntity show(ReceiptEntity receipt);


}
package kr.admin.serviceImpl;


import jakarta.transaction.Transactional;

import kr.admin.absent.chart.CostModel;
import kr.admin.component.RestaurantModel;
import kr.admin.config.RestTemplateConfig;
import kr.admin.entity.ReceiptEntity;
import kr.admin.entity.RestaurantEntity;
import kr.admin.repository.ReceiptRepository;
import kr.admin.repository.RestaurantRepository;
import kr.admin.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository repository;
    private final RestaurantRepository restaurantRepository;
    private final RestTemplateConfig restTemplateConfig;

    @Override
    public RestaurantModel save(ReceiptEntity receipt) {
        ReceiptEntity existingReceipt = repository.findByDate(receipt.getDate());

        if (existingReceipt != null && existingReceipt.getName().equals(receipt.getName())) {
            return null;
        }
        ReceiptEntity savedReceipt = repository.save(receipt);
        String restaurantName = savedReceipt.getName();
        System.out.println(restaurantName);

        Long restaurantId = repository.findRestaurantId(restaurantName);
        Optional<RestaurantEntity> restaurantEntity = restaurantRepository.findById(restaurantId);

        System.out.println(restaurantEntity);

        return restaurantEntity.map(RestaurantModel::toDto).orElse(null);
    }

    public ReceiptEntity show(ReceiptEntity receipt) {
        ReceiptEntity existingReceipt = repository.findByDate(receipt.getDate());

        if (existingReceipt != null && existingReceipt.getName().equals(receipt.getName())) {
            return existingReceipt;
        } else return null;
    }


    @Override
    @Transactional
    public List<CostModel> costModelList(String userId) {
        return repository.costList(userId);
    }

    @Override
    public List<ReceiptEntity> findByUserId(String id) {
        return repository.findByUserId(id);
    }

    @Override
    public Boolean deleteById(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public ReceiptEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }


}
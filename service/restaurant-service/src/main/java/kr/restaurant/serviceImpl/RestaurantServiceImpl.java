package kr.restaurant.serviceImpl;

import kr.restaurant.component.RestaurantModel;
import kr.restaurant.entity.RestaurantEntity;
import kr.restaurant.repository.RestaurantRepository;
import kr.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;



    @Override
    public List<RestaurantEntity> getCrawlingInfos() {
        return List.of();
    }

    @Override
    public List<RestaurantModel> findAll() {
        List<RestaurantEntity> entities = restaurantRepository.findAll();
        return entities.stream()
                .map(RestaurantModel::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<RestaurantModel> searchRestaurants(String keyword) {
        List<RestaurantEntity> entities = restaurantRepository.searchRestaurant(keyword);
        return entities.stream()
                .map(RestaurantModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantModel findById(Long id) {
        Optional<RestaurantEntity> entity = restaurantRepository.findById(id);
        return entity.map(RestaurantModel::toDto).orElse(null);
    }


    @Override
    public Boolean existsById(Long id) {
        if(restaurantRepository.existsById(id)) {
            return true;
        }
        return false;
    }


    @Override
    public ResponseEntity<RestaurantModel> getOneRestaurant(@PathVariable Long id) {
        Optional<RestaurantEntity> restaurantEntityOpt = restaurantRepository.findById(id);
        if (restaurantEntityOpt.isPresent()) {
            RestaurantEntity restaurantEntity = restaurantEntityOpt.get();
            RestaurantModel restaurantModel = RestaurantModel.toDto(restaurantEntity);

            return ResponseEntity.ok(restaurantModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public List<RestaurantModel> getRestaurantsByTag(List<String> tagNames) {
        List<RestaurantEntity> entities = restaurantRepository.findByTagName(tagNames);
        return entities.stream()
                .map(RestaurantModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantModel> findByCategory(List<String> categories) {
        List<RestaurantEntity> entities = restaurantRepository.findByCategoryUsingMenu(categories);
        return entities.stream()
                .map(RestaurantModel::toDto)
                .collect(Collectors.toList());
    }




}

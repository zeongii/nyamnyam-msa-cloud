package kr.restaurant.repository;


import kr.restaurant.entity.RestaurantEntity;
import kr.restaurant.repositoryCustom.RestaurantRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity,Long> , RestaurantRepositoryCustom {


    @Query("SELECT r.name FROM RestaurantEntity r")
    List<String> findAllNames();

    List<RestaurantEntity> findByCategoryUsingMenu(List<String> categories);


}

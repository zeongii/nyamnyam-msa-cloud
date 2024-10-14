package kr.admin.repository;


import kr.admin.entity.RestaurantEntity;
import kr.admin.repositoryCustom.RestaurantRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity,Long>, RestaurantRepositoryCustom {
}

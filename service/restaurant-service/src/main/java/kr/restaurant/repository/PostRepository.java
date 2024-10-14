package kr.restaurant.repository;

import jakarta.transaction.Transactional;

import kr.restaurant.entity.PostEntity;
import kr.restaurant.repositoryCustom.PostRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<PostEntity, Long>, PostRepositoryCustom {

    List<PostEntity> findByRestaurantId(Long restaurantId);

    @Query("SELECT p FROM PostEntity p WHERE DATE(p.entryDate) = CURRENT_DATE")
    List<PostEntity> findPostsByToday();
}


package kr.restaurant.repository;



import kr.restaurant.entity.WishListEntity;
import kr.restaurant.repositoryCustom.WishListCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WishListRepository extends JpaRepository<WishListEntity, String>, WishListCustomRepository {

    boolean existsByName(String name);

    boolean existsByIdAndUserId(Long wishListId, String userId);

    boolean existsByNameAndUserId(String name, String userId);



}

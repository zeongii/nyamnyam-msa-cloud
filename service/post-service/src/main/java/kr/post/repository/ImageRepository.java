package kr.post.repository;

import kr.post.entity.ImageEntity;
import kr.post.repositoryCustom.ImageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long>, ImageRepositoryCustom {

    List<ImageEntity> findByPostId(Long postId);

    Boolean existsByPostId(Long postId);

    Boolean deleteByPostId(Long postId);

    List<ImageEntity> findByPostIdIn(List<Long> postIds);
}

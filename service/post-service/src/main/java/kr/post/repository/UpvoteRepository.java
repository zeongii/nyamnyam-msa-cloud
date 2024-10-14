package kr.post.repository;

import kr.post.entity.UpvoteEntity;
import kr.post.repositoryCustom.UpvoteRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpvoteRepository extends JpaRepository<UpvoteEntity, Long>, UpvoteRepositoryCustom {

    boolean existsByPostIdAndGiveId(Long postId, String userId);

    Optional<UpvoteEntity> findByPostIdAndGiveId(Long postId, String userId);

    int countByPostId(Long postId);
}

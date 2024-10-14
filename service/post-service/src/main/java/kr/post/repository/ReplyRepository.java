package kr.post.repository;

import kr.post.entity.ReplyEntity;
import kr.post.repositoryCustom.ReplyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long>, ReplyRepositoryCustom {

    List<ReplyEntity> findByPostId(Long postId);

}


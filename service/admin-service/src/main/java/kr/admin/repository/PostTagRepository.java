package kr.admin.repository;


import kr.admin.entity.PostEntity;
import kr.admin.entity.PostTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {
    List<PostTagEntity> findByPostId(Long postId);
    void deleteByPostId(Long postId);

    List<PostTagEntity> findByPost(PostEntity postEntity);
}

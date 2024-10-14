package kr.post.repository;

import kr.post.entity.PostEntity;
import kr.post.entity.PostTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {
    List<PostTagEntity> findByPostId(Long postId);
    void deleteByPostId(Long postId);

    List<PostTagEntity> findByPost(PostEntity postEntity);
}

package kr.post.repositoryCustom;

import java.util.List;

public interface ImageRepositoryCustom {
    List<Long> findImageIdsByPostId(Long postId);
}

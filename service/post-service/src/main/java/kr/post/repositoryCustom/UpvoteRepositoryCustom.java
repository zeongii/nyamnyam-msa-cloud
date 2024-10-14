package kr.post.repositoryCustom;

import kr.post.entity.UpvoteEntity;

public interface UpvoteRepositoryCustom {

    UpvoteEntity save(Long postId, String giveId, String haveId);
}

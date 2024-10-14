package kr.post.repositoryCustom;

import com.querydsl.core.Tuple;

import java.util.List;

public interface ReplyRepositoryCustom {
    List<Tuple> findAllByPostWithNickname(Long postId);

    Tuple findByIdWithNickname(Long replyId);
}

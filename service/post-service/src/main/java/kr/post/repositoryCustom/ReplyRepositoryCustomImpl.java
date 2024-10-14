package kr.post.repositoryCustom;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.post.entity.ReplyEntity;
import kr.post.entity.QReplyEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReplyRepositoryCustomImpl implements ReplyRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findAllByPostWithNickname(Long postId) {
        QReplyEntity replyEntity = QReplyEntity.replyEntity;

        List<Tuple> result = jpaQueryFactory
                .select(replyEntity, replyEntity.nickname)
                .from(replyEntity)
                .where(replyEntity.postId.eq(postId))
                .fetch();

        String queryString = jpaQueryFactory
                .select(replyEntity, replyEntity.nickname)
                .from(replyEntity)
                .where(replyEntity.postId.eq(postId))
                .toString();

        result.forEach(tuple -> {
            ReplyEntity reply = tuple.get(replyEntity);
            String userNickname = tuple.get(replyEntity.nickname);
        });
        return result;
    }

    @Override
    public Tuple findByIdWithNickname(Long replyId){
        QReplyEntity replyEntity = QReplyEntity.replyEntity;
        return jpaQueryFactory
                .select(replyEntity, replyEntity.nickname)
                .from(replyEntity)
                .where(replyEntity.id.eq(replyId))
                .fetchOne();
    }
}

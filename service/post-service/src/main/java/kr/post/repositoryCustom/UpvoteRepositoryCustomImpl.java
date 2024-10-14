package kr.post.repositoryCustom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import kr.post.entity.PostEntity;
import kr.post.entity.QPostEntity;
import kr.post.entity.UpvoteEntity;
import kr.post.repository.UpvoteRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpvoteRepositoryCustomImpl implements UpvoteRepositoryCustom {

    private JPAQueryFactory jpaQueryFactory;
    private UpvoteRepository upvoteRepository;


    @Override
    public UpvoteEntity save(Long postId, String giveId, String haveId) {
        QPostEntity postEntity = QPostEntity.postEntity;

        PostEntity post = jpaQueryFactory
                .select(postEntity)
                .from(postEntity)
                .where(postEntity.id.eq(postId))
                .fetchOne();

        if (post == null) {
            throw new EntityNotFoundException("Post not found for ID: " + postId);
        }

        UpvoteEntity newUpvote = new UpvoteEntity();
        newUpvote.setPostId(post.getId());
        newUpvote.setGiveId(giveId);
        newUpvote.setHaveId(haveId);

        return upvoteRepository.save(newUpvote);
    }
}

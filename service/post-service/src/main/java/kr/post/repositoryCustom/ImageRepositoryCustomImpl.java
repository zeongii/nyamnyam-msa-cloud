package kr.post.repositoryCustom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.post.entity.QImageEntity;
import kr.post.entity.QPostEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ImageRepositoryCustomImpl implements ImageRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    // post id에 대한 image id 조회하기
    @Override
    public List<Long> findImageIdsByPostId(Long postId) {
        QImageEntity image = QImageEntity.imageEntity;
        QPostEntity post= QPostEntity.postEntity;

        return queryFactory
                .select(image.id)
                .from(image)
                .join(image.post, post)
                .where(post.id.eq(postId))
                .fetch();
    }
}

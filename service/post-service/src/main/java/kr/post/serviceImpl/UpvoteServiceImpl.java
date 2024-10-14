package kr.post.serviceImpl;

import kr.post.entity.UpvoteEntity;
import kr.post.repository.PostRepository;
import kr.post.entity.PostEntity;
import kr.post.repository.UpvoteRepository;
import kr.post.service.UpvoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpvoteServiceImpl implements UpvoteService {
    private final UpvoteRepository repository;
    private final PostRepository postRepository;

    @Override
    public boolean like(Long postId, String userId) {
        if(!repository.existsByPostIdAndGiveId(postId, userId)){

            String haveId = postRepository.findById(postId)
                    .map(PostEntity::getUserId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if(userId.equals(haveId)){
                throw new RuntimeException("본인의 리뷰에는 좋아요를 누를 수 없어요.");
            }

            UpvoteEntity upvote = UpvoteEntity.builder()
                    .postId(postId)
                    .giveId(userId)
                    .haveId(haveId)
                    .build();
            repository.save(upvote);

            return true;
        } return false;
    }

    @Override
    public boolean unlike(Long postId, String userId) {
        UpvoteEntity upvote = repository.findByPostIdAndGiveId(postId,userId)
                .orElse(null);

        if(upvote != null) {
            repository.delete(upvote);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasLiked(Long postId, String userId) {
        return repository.existsByPostIdAndGiveId(postId, userId);
    }

    @Override
    public int getLikeCount(Long postId) {
        return repository.countByPostId(postId);
    }
}

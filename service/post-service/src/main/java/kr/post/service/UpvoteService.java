package kr.post.service;

public interface UpvoteService {
    boolean like(Long postId, String userId);

    boolean unlike(Long postId, String userId);

    boolean hasLiked(Long postId, String userId);

    int getLikeCount(Long postId);
}

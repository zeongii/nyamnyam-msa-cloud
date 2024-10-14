package kr.post.service;

import kr.post.component.ReplyModel;
import kr.post.entity.ReplyEntity;

import java.util.List;

public interface ReplyService {

    List<ReplyModel> findAll();

    ReplyModel findById(Long id);

    Boolean existsById(Long id);

    Long count();

    Boolean deleteById(Long id);

    ReplyModel save(ReplyModel model);

    ReplyModel update(Long id, ReplyModel model);

    List<ReplyModel> findAllByPostId(Long postId);

    ReplyEntity convertToEntity(ReplyModel model);
}
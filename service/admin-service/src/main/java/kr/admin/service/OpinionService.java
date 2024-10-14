package kr.admin.service;




import kr.admin.component.OpinionModel;
import kr.admin.entity.OpinionEntity;

import java.util.List;

public interface OpinionService {

    List<OpinionEntity> findAll();

    OpinionEntity findById(Long id);

    Long count();

    OpinionEntity save(OpinionModel model);
}

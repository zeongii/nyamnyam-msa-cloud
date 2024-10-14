package kr.admin.service;


import kr.admin.component.NoticeModel;
import kr.admin.entity.NoticeEntity;

import java.util.List;

public interface NoticeService {

    List<NoticeEntity> findAll();

    NoticeEntity findById(Long id);

    Long count();

    Boolean deleteById(Long id);

    NoticeEntity save(NoticeModel model);

    NoticeEntity update(NoticeModel model);

}

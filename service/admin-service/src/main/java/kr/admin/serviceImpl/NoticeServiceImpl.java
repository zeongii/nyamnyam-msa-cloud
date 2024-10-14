package kr.admin.serviceImpl;


import jakarta.transaction.Transactional;

import kr.admin.component.NoticeModel;
import kr.admin.entity.NoticeEntity;
import kr.admin.repository.NoticeRepository;
import kr.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeEntity> findAll() {
       return noticeRepository.findAll();
    }

    @Override
    @Transactional
    public NoticeEntity findById(Long id) {
        noticeRepository.updateHits(id);
        return noticeRepository.findById(id).orElse(null);
    }



    @Override
    public Long count() {
        return noticeRepository.count();
    }


    @Override
    public Boolean deleteById(Long id) {
        if (noticeRepository.existsById(id)) {
            noticeRepository.deleteById(id);
        }
            return false;
    };

    @Override
    public NoticeEntity save(NoticeModel model) {
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title(model.getTitle())
                .content(model.getContent())
                .hits(model.getHits())
                .date(LocalDateTime.now())
                .build();

        return noticeRepository.save(noticeEntity);
    }

    @Override
    public NoticeEntity update(NoticeModel model) {
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .id(model.getId())
                .content(model.getContent())
                .title(model.getTitle())
                .hits(model.getHits())
                .date(LocalDateTime.now())
                .build();

        return noticeRepository.save(noticeEntity);
    }



}

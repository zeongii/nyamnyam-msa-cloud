package kr.admin.serviceImpl;



import kr.admin.component.OpinionModel;
import kr.admin.entity.OpinionEntity;
import kr.admin.repository.OpinionRepository;
import kr.admin.service.OpinionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpinionServiceImpl implements OpinionService {

    private final OpinionRepository opinionRepository;


    @Override
    public List<OpinionEntity> findAll() {
        return opinionRepository.findAll();
    }

    @Override
    public OpinionEntity findById(Long id) {
        return opinionRepository.findById(id).orElse(null);
    }

    @Override
    public Long count() {
        return opinionRepository.count();
    }


    @Override
    public OpinionEntity save(OpinionModel model) {
        OpinionEntity report = OpinionEntity.builder()
                .content(model.getContent())
                .userId(model.getUserId())
                .entryDate(LocalDateTime.now())
                .build();

        return opinionRepository.save(report);
    }
}

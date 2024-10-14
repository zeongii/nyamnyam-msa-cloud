package kr.admin.repository;


import kr.admin.entity.ReceiptEntity;
import kr.admin.repositoryCustom.ReceiptRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long>, ReceiptRepositoryCustom {

    ReceiptEntity findByDate(String date);

    List<ReceiptEntity> findByUserId(String id);

}

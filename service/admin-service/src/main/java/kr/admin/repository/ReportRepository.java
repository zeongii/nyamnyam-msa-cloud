package kr.admin.repository;


import kr.admin.entity.ReportEntity;
import kr.admin.repositoryCustom.ReportRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long>, ReportRepositoryCustom {

    ReportEntity findByPostId(Long postId);

}

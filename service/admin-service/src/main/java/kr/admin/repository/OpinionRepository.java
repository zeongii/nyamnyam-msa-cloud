package kr.admin.repository;


import kr.admin.entity.OpinionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionRepository extends JpaRepository<OpinionEntity, Long> {
}

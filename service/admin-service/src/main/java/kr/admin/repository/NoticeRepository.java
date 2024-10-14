package kr.admin.repository;


import kr.admin.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

    @Modifying
    @Query(value = "update NoticeEntity n set n.hits = n.hits+1 where n.id=:id")
    void updateHits(@Param("id") Long id);

}

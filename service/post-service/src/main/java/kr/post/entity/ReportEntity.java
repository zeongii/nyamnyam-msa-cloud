package kr.post.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import kr.post.component.ReportModel;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "report")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")  // FK 설정
    private PostEntity post;

    private String reason;

    @Column(columnDefinition = "TIMESTAMP(0)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime date;


    public static ReportEntity toReportEntity(ReportModel reportModel, PostEntity postEntity) {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setId(reportModel.getId());
        reportEntity.setPost(postEntity);
        reportEntity.setReason(reportModel.getReason());
        reportEntity.setUserId(reportModel.getUserId());
        return reportEntity;
    }

    public ReportModel toReportModel() {
        ReportModel reportModel = new ReportModel();
        reportModel.setId(this.id);
        reportModel.setUserId(this.userId);
        reportModel.setPostId(this.post != null ? this.post.getId() : null);
        reportModel.setReason(this.reason);
        reportModel.setEntryDate(this.date);
        return reportModel;
    }





}

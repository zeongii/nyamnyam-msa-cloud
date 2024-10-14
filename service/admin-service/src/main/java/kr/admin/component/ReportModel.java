package kr.admin.component;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class ReportModel {

    private Long id;
    private String userId;
    private Long postId;
    private String reason;
    private LocalDateTime entryDate;

}

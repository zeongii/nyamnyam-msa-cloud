package kr.admin.component;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
@Builder
@NoArgsConstructor
public class NoticeModel {
    private Long id;
    private String title;
    private String content;
    private Long hits;
    private Date date;

    @Builder
    public NoticeModel(Long id, String title, String content, Long hits, Date date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.hits = hits;
    }

}





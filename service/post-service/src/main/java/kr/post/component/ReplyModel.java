package kr.post.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyModel {
    private Long id;
    private String content;

    private LocalDateTime entryDate;
    private LocalDateTime modifyDate;

    private Long postId;

    private String userId;
    private String nickname;

}


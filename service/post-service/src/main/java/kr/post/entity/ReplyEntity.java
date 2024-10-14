package kr.post.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "replies")
public class ReplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @Column(name = "user_id")
    private String userId;
    private String nickname;

    @Column(name = "post_id")
    private Long postId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime entryDate;
    private LocalDateTime modifyDate;

    @PrePersist
    private void onCreate(){
        this.entryDate = LocalDateTime.now();
        this.modifyDate = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate(){
        this.modifyDate = LocalDateTime.now();
    }

}

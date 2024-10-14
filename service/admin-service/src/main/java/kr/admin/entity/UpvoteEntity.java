package kr.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Table(name = "upvote")
public class UpvoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private String giveId;
    private String haveId;

}

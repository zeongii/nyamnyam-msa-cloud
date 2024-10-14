package kr.post.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Table(name = "post_tags")
public class PostTagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_name", referencedColumnName = "name", nullable = false)
    @JsonBackReference
    private TagEntity tag;

    public PostTagEntity(PostEntity entity, TagEntity tag) {
        this.post = entity;
        this.tag = tag;
    }
    public TagEntity getTag() {
        return tag;
    }
}

package kr.post.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long taste;
    private Long clean;
    private Long service;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime entryDate;
    private LocalDateTime modifyDate;

    private String userId;
    private String nickname;

    @PrePersist
    protected void onCreate() {
        this.entryDate = LocalDateTime.now();
        this.modifyDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PostTagEntity> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ImageEntity> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ReportEntity> reportEntities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")  // FK 설정
    private RestaurantEntity restaurant;







}

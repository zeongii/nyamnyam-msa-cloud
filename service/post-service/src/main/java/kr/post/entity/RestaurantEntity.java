package kr.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@RequiredArgsConstructor
@Entity
@Data
@Table(name = "restaurant")
@AllArgsConstructor
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String type;
    private Double rate;

    @Column(length = 500)
    private String operation;
    private String tel;
    private String menu;
    @Column(length = 500)
    private String thumbnailImageUrl;
    @Column(length = 500)
    private String subImageUrl;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PostEntity> posts = new ArrayList<>();

}

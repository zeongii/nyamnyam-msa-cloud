package kr.post.component;


import kr.post.entity.RestaurantEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantModel {

    private Long id;
    private String name;
    private String address;
    private String type;
    private Double rate;
    private String operation;
    private String tel;
    private String menu;
    private String thumbnailImageUrl;
    private String subImageUrl;

    // 생성자
    @Builder
    public RestaurantModel(Long id, String name, String address, String type, Double rate, String operation, String tel, String menu, String thumbnailImageUrl, String subImageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.rate = rate;
        this.operation = operation;
        this.tel = tel;
        this.menu = menu;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.subImageUrl = subImageUrl;
    }

    // DTO를 엔티티로 변환하는 메서드
    public RestaurantEntity toEntity() {
        return RestaurantEntity.builder()
                .id(id)
                .name(name)
                .address(address)
                .type(type)
                .rate(rate)
                .operation(operation)
                .tel(tel)
                .menu(menu)
                .thumbnailImageUrl(thumbnailImageUrl)
                .subImageUrl(subImageUrl)
                .build();
    }

    // 엔티티를 DTO로 변환하는 메서드
    public static RestaurantModel toDto(RestaurantEntity entity) {
        return RestaurantModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .type(entity.getType())
                .rate(entity.getRate())
                .operation(entity.getOperation())
                .tel(entity.getTel())
                .menu(entity.getMenu())
                .thumbnailImageUrl(entity.getThumbnailImageUrl())
                .subImageUrl(entity.getSubImageUrl())
                .build();
    }
}

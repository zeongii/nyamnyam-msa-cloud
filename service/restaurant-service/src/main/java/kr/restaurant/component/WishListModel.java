package kr.restaurant.component;



import kr.restaurant.entity.WishListEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WishListModel {
    private Long id;

    private String name;
    private String userId;

    @Builder
    public WishListModel(Long id, String name, String userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }


    public WishListEntity toEntity() {
        return WishListEntity.builder()
                .id(this.id)
                .name(this.name)
                .userId(this.userId)
                .build();
    }

    public static WishListModel toDto(WishListEntity entity) {
        return WishListModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .userId(entity.getUserId())
                .build();
    }

}

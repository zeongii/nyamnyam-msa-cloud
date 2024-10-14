package kr.restaurant.component;



import kr.restaurant.entity.WishListRestaurantEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WishListRestaurantModel {
    private Long id;

    private Long wishListId;
    private Long restaurantId;
    private String userId;

    @Builder
    public WishListRestaurantModel(Long id,Long wishListId, Long restaurantId, String userId) {
        this.id = id;
        this.wishListId = wishListId;
        this.restaurantId = restaurantId;
        this.userId = userId;
    }

    public WishListRestaurantEntity toEntity() {
        return WishListRestaurantEntity.builder()
                .id(this.id)
                .wishListId(this.wishListId)
                .restaurantId(this.restaurantId)
                .userId(this.userId)
                .build();
    }


    public static WishListRestaurantModel toDto(WishListRestaurantEntity entity) {
        return WishListRestaurantModel.builder()
                .id(entity.getId())
                .wishListId(entity.getWishListId())
                .restaurantId(entity.getRestaurantId())
                .userId(entity.getUserId())
                .build();
    }
}

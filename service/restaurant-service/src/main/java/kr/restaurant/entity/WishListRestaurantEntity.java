package kr.restaurant.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "wishListsRestaurants")
public class WishListRestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long wishListId;
    private Long restaurantId;
    private String userId;


}

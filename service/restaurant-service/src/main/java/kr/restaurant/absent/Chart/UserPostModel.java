package kr.restaurant.absent.Chart;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserPostModel {
    private Long postId;
    private String content;
    private String name;
    private Long restaurantId;
    private LocalDateTime entryDate;
    private Long upvoteCount;


}

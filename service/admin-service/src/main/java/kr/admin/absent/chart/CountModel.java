package kr.admin.absent.chart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountModel {
    private String nickname;
    private Long count;

    public CountModel(String nickname, Long count) {
        this.nickname = nickname;
        this.count = count;
    }


}

package kr.admin.component;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
public class OpinionModel {
    private Long id;
    private String userId;
    private String content;
    private Date entryDate;

}

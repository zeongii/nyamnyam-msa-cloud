package kr.admin.component;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;


@Data
@Component
public class ReceiptModel {

    private Long id;
    private Long price;
    private String name;
    private String menu;
    private String userId;
    private String date;
    private Date entryDate;


}

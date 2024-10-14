package kr.admin.absent;/*
package kr.nyamnyam.ocr;

import kr.nyamnyam.model.domain.ImageModel;
import kr.nyamnyam.model.domain.ReceiptModel;
import kr.nyamnyam.model.domain.RestaurantModel;
import kr.nyamnyam.model.entity.ReceiptEntity;
import kr.nyamnyam.service.ImageService;
import kr.nyamnyam.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt")
@CrossOrigin(origins = "http://localhost:3000")
public class OCRController {

    private final NaverOcrApi naverOcrApi;
    private final ImageService imageService;
    private final ReceiptService receiptService;


    @Value("${naver.service.secretKey}")
    private String secretKey;

    private static final Logger log = LoggerFactory.getLogger(OCRController.class);

    @PostMapping("/insert")
    public ResponseEntity<RestaurantModel> insertReceipt(@RequestParam("file") MultipartFile file) throws IOException {
        ImageModel imageModel = imageService.insertReceipt(file);
        String storedFileName = imageModel.getStoredFileName();
        System.out.println(storedFileName);

        File file1 = new File("src/main/resources/static/image/" + storedFileName);
        if (!file1.exists()) {
            throw new RuntimeException("File does not exist: " + file1.getPath());
        }

        List<String> result = naverOcrApi.callApi(file1.getPath(), secretKey, "jpg");
        if (result != null) {
            for (String s : result) {
                log.info(s);
            }
        } else {
            log.info("No result returned from OCR API.");
        }

        int startNameIndex = result.indexOf("[매장명]") + 1;
        int endNameIndex = result.indexOf("[사업자]");

        String name = result.subList(startNameIndex, endNameIndex).stream()
                .collect(Collectors.joining(" "));


        Long price = IntStream.range(0, result.size())
                .filter(i -> i < result.size() - 2 &&
                        result.get(i).equals("합계") &&
                        result.get(i + 1).equals("금액"))
                .mapToObj(i -> result.get(i + 2).replace(",", ""))
                .map(Long::valueOf)
                .findFirst()
                .orElse(0L);

        StringBuilder menuBuilder = new StringBuilder();

        int startMenuIndex = result.indexOf("금액") + 1;
        int endMenuIndex = result.indexOf("합계");

        for (int i = startMenuIndex; i < endMenuIndex; i++) {
            if ((i - startMenuIndex) % 4 == 0) {
                menuBuilder.append(result.get(i)).append(", ");
            }
        }

        if (menuBuilder.length() > 0) {
            menuBuilder.setLength(menuBuilder.length() - 2);
        }
        String menu = menuBuilder.toString();


        int startDateIndex = result.indexOf("승인일시:") + 1;
        int endDateIndex = result.indexOf("가맹점");

        String date = result.subList(startDateIndex, endDateIndex).stream()
                .collect(Collectors.joining(" "));


        ReceiptEntity receipt = ReceiptEntity.builder()
                .name(name)
                .menu(menu)
                .price(price)
                .date(date)
                .entryDate(LocalDateTime.now())
                .build();

        System.out.println("menu = " + menu + " name =" + name + " price =" + price + " date =" + date);
        return ResponseEntity.ok(receiptService.save(receipt));
    }


}*/

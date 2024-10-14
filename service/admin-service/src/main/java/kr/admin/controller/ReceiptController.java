package kr.admin.controller;


import kr.admin.absent.NaverOcrApi;
import kr.admin.absent.chart.CostModel;
import kr.admin.component.ImageModel;
import kr.admin.entity.ReceiptEntity;
import kr.admin.service.ReceiptService;
import kr.admin.service.ImageService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt")
@CrossOrigin(origins = "http://localhost:3000")
public class ReceiptController {

    private final NaverOcrApi naverOcrApi;
    private final ImageService imageService;
    private final ReceiptService receiptService;


    @Value("${naver.service.secretKey}")
    private String secretKey;

    @GetMapping("/wallet/{id}")
    public ResponseEntity<List<?>> myWallet(@PathVariable String id) {
        return ResponseEntity.ok(receiptService.findByUserId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReceipt(@PathVariable Long id){
        return ResponseEntity.ok(receiptService.deleteById(id));
    }

    @GetMapping("/wallet/cost/{id}")
    public ResponseEntity<List<CostModel>> costList(@PathVariable String id) {
        return ResponseEntity.ok(receiptService.costModelList(id));
    }


    @PostMapping("/insert/{id}")
    public ResponseEntity<?> insertReceipt(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        ImageModel imageModel = imageService.insertReceipt(file);
        String storedFileName = imageModel.getStoredFileName();

        File file1 = new File("src/main/resources/static/image/" + storedFileName);
        if (!file1.exists()) {
            throw new RuntimeException("File does not exist: " + file1.getPath());
        }

        List<String> result = naverOcrApi.callApi(file1.getPath(), secretKey, "jpg");

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
                .collect(Collectors.joining(""));
        if (date.length() > 10) {
            date = date.substring(0, 10) + " " + date.substring(10);
        }


        ReceiptEntity receipt = ReceiptEntity.builder()
                .name(name)
                .menu(menu)
                .price(price)
                .date(date)
                .entryDate(LocalDateTime.now())
                .userId(id)
                .build();



        System.out.println("menu = " + menu + " name =" + name + " price =" + price + " date =" + date);
        return ResponseEntity.ok(receiptService.save(receipt));
    }

    @GetMapping("/show")
    public ResponseEntity<ReceiptEntity> show(@RequestBody ReceiptEntity receipt) {
        return ResponseEntity.ok(receiptService.show(receipt));
    }

















}
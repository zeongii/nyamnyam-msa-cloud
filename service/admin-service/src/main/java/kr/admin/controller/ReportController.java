package kr.admin.controller;


import kr.admin.component.ReportModel;
import kr.admin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/save")
    public ResponseEntity<Boolean> reportComment(@RequestBody ReportModel reportModel) {
        return ResponseEntity.ok(reportService.save(reportModel));

    }

    @GetMapping("")
    public ResponseEntity<List<?>> reportList() {
        return ResponseEntity.ok(reportService.findAll());
    }

    @GetMapping("/reportAll")
    public ResponseEntity<List<?>> reportAll () {
        return ResponseEntity.ok(reportService.reportAll());
    }

}

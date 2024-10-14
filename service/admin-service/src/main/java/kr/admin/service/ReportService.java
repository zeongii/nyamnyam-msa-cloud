package kr.admin.service;



import kr.admin.absent.chart.ReportCountModel;
import kr.admin.component.ReportModel;

import java.util.List;

public interface ReportService {

    Boolean save(ReportModel model);

    List<ReportModel> findAll();

    List<ReportCountModel> reportAll();
}

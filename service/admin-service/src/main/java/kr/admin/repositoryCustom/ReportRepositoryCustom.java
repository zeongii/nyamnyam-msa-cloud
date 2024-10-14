package kr.admin.repositoryCustom;


import kr.admin.absent.chart.ReportCountModel;

import java.util.List;

public interface ReportRepositoryCustom {

    List<ReportCountModel> reportFindAll();
}

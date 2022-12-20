package org.mentpeak.test.feign;

import lombok.AllArgsConstructor;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.test.service.ReportUserService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报告相关服务Feign实现类
 *
 * @author demain_lee
 * @since 2022-09-08
 */
@RestController
@AllArgsConstructor
public class ReportClient implements IReportClient {

  private final ReportUserService reportUserService;

  @Override
  public Result<Boolean> updatePersonalReportsParentRating(Long userId) {
    return Result.status(reportUserService.updatePersonalReportsParentRating(userId));
  }
}

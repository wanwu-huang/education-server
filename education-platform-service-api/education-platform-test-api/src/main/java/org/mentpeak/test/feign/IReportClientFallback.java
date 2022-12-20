package org.mentpeak.test.feign;

import org.mentpeak.core.tool.api.Result;
import org.springframework.stereotype.Component;

/**
 * 远程调用失败，降级
 */
@Component
public class IReportClientFallback implements IReportClient {

  @Override
  public Result<Boolean> updatePersonalReportsParentRating(Long userId) {
    return Result.fail("修改个人报告家长他评数据失败");
  }
}

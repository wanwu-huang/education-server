package org.mentpeak.test.feign;

import org.mentpeak.core.tool.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 报告相关服务 Feign接口类
 *
 * @author demain_lee
 * @since 2022-09-08
 */
@FeignClient(
    value = "education-test",
    fallback = IReportClientFallback.class
)
public interface IReportClient {

  String API_PREFIX = "/client";
  String Reports_Parent_Rating = API_PREFIX + "/updatePersonalReportsParentRating";


  /**
   * 修改个人报告家长他评数据
   *
   * @param userId 学生用户id
   * @return
   */
  @PostMapping(Reports_Parent_Rating)
  Result<Boolean> updatePersonalReportsParentRating(
      @RequestParam("userId") Long userId);


}

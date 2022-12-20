package org.mentpeak.test.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;

/**
 * 报告相关
 * @author demain_lee
 * @since 2022-09-01
 */
public interface ReportGroupsMapper{

  /**
   * 获取年级名字
   * @param id id
   * @return
   */
	String gradeNameById(Integer id);

  /**
   * 统计男女学生数量
   * @param sex
   * @return
   */
  @InterceptorIgnore(tenantLine = "true")
  Integer statisticsCount(@Param("sex") Integer sex,@Param("taskId") Long taskId);
}

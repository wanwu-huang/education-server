package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 个人报告列表数据
 *
 * @author demain_lee
 * @since 2022-09-06
 */
@Data
public class ReportUserVO {

  @ApiModelProperty(value = "试卷ID")
  private Long paperId;

  @ApiModelProperty(value = "测评时间")
  private String startTime;

  @ApiModelProperty(value = "任务ID")
  private Long taskId;

  @ApiModelProperty(value = "用户ID")
  private Long userId;

  @ApiModelProperty(value = "学籍号")
  private String account;

  @ApiModelProperty(value = "姓名")
  private String realName;

  @ApiModelProperty(value = "性别")
  private Long sex;

  @ApiModelProperty(value = "性别名字")
  private String sexName;

  @ApiModelProperty(value = "年级ID")
  private Long grade;

  @ApiModelProperty(value = "班级ID")
  private String classId;

  @ApiModelProperty(value = "年级名字")
  private String gradeName;

  @ApiModelProperty(value = "班级名字")
  private String className;

  @ApiModelProperty(value = "报告Id")
  private String reportId;

}

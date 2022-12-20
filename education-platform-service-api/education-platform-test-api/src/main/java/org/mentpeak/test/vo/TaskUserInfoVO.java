package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户数据
 *
 * @author demain_lee
 * @since 2022-08-23
 */
@Data
public class TaskUserInfoVO {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "年级ID")
    private String grade;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "姓名")
    private String realName;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ApiModelProperty(value = "年级名字")
    private String gradeName;

    @ApiModelProperty(value = "班级名字")
    private String className;

    @ApiModelProperty(value = "试卷ID")
    private Long paperId;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String finishTime;
}

package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务管理信息
 * @author: demain_lee
 * @since:  2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskInfoVO extends TaskListVO{

    @ApiModelProperty(value = "测评人数")
    private Integer testPeopleCount;

    @ApiModelProperty(value = "完成人数")
    private Integer completionPeopleCount;

    @ApiModelProperty(value = "任务状态0:未完成 1:已完成")
    private Integer taskStatus;

    @ApiModelProperty(value = "任务状态名字")
    private String taskStatusName;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty(value = "是否有报告 0 没有 1 有")
    private Integer isReport;
}

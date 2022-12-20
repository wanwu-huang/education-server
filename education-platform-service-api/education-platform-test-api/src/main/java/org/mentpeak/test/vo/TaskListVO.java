package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月13日14:26
 * 任务列表
 */
@Data
public class TaskListVO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    @ApiModelProperty(value = "开始时间")
    private String beginTime;
    @ApiModelProperty(value = "截至时间")
    private String endTime;
    @ApiModelProperty(value = "任务状态0:未完成 1:已完成 2:未开始")
    private Integer status;
}

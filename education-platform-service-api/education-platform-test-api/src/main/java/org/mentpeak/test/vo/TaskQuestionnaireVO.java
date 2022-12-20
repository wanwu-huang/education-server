package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月13日19:05
 */
@Data
public class TaskQuestionnaireVO implements Serializable {

    @ApiModelProperty(value = "任务Id")
    private Long taskId;

    @ApiModelProperty(value = "量表id")
    private Long questionnaireId;

    @ApiModelProperty(value = "量表名称")
    private String name;

    @ApiModelProperty(value = "是否完成 0:否 1:是")
    private Integer isFinish;
}

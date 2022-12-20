package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月16日8:56
 */
@Data
public class GenerateVO implements Serializable {

    @ApiModelProperty(value = "任务 ID")
    private Long taskId;

    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;

    @ApiModelProperty(value = "试卷 ID")
    private Long paperId;

    @ApiModelProperty(value = "模块id", required = true)
    private Long moduleId;
}

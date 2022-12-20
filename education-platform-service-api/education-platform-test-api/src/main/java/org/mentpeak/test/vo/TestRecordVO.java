package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月13日17:43
 */
@Data
public class TestRecordVO implements Serializable {

    @ApiModelProperty(value = "任务id")
    private Long taskId;
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    @ApiModelProperty(value = "问卷id")
    private Long questionnaireId;
    @ApiModelProperty(value = "量表名称")
    private String name;
    @ApiModelProperty(value = "测评时间")
    private String testTime;
    @ApiModelProperty(value = "报告可见 0:可见 1:不可见")
    private Integer isCheck;
    @ApiModelProperty(value = "报告id")
    private String reportId;
}

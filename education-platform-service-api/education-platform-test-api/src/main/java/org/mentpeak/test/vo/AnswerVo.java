package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @data 2021年12月03日14:02
 */
@Data
public class AnswerVo {

    @ApiModelProperty(value = "下一题模块id", required = true)
    private Long moduleId;

    @ApiModelProperty(value = "是否结束该试卷 0:未结束 1:结束该问卷(跳指导语)  2:结束试卷")
    private Integer isFinished = 0;

}

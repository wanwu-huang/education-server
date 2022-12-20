package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月15日14:26
 */
@Data
public class NextQuestionDTO implements Serializable {

    @ApiModelProperty(value = "问卷ID", required = true)
    private Long questionnaireId;

    @ApiModelProperty(value = "试卷 ID", required = true)
    private Long paperId;

    @ApiModelProperty(value = "模块id", required = true)
    private Long moduleId;

    @ApiModelProperty(value = "模块下标", required = true)
    private int moduleIndex;

    @ApiModelProperty(value = "数据有效性模块次数", required = true)
    private int moduleCount;
}

package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月15日14:23
 */
@Data
public class QuestionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;

    @ApiModelProperty(value = "试卷 ID")
    private Long paperId;

    @ApiModelProperty(value = "模块id", required = true)
    private Long moduleId;

    @ApiModelProperty(value = "题干 ID")
    private Long qid;

    @ApiModelProperty(value = "题型")
    private Long qtype;

    @ApiModelProperty(value = "答案ID")
    private Long oid;

    @ApiModelProperty(value = "是否有下一题 0没有 1有")
    private Integer isNextId;

    @ApiModelProperty(value = "模块下标", required = true)
    private int moduleIndex;

    @ApiModelProperty(value = "数据有效性模块次数", required = true)
    private int moduleCount;
}

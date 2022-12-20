package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.test.base.BaseOption;
import org.mentpeak.test.base.BaseQuestion;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年07月14日15:46
 */
@Data
@ApiModel(value = "QuestionVO", description = "试卷题干信息")
public class QuestionVO extends BaseQuestion implements Serializable {

    @ApiModelProperty(value = "问卷ID")
    private Long questionnaireId;

    @ApiModelProperty(value = "试卷 ID")
    private Long paperId;

    @ApiModelProperty("题支")
    private List<BaseOption> optionList;

    @ApiModelProperty("模块id")
    private Long moduleId;

    @ApiModelProperty("模块下标")
    private int moduleIndex;

    @ApiModelProperty("数据有效性模块次数")
    private int moduleCount;

    @ApiModelProperty(value = "是否有下一题 0没有 1有")
    private int isNextId = 1;
}

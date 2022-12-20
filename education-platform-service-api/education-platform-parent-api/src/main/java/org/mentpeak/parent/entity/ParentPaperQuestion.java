package org.mentpeak.parent.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

import java.util.List;

/**
 * 试卷和题干及选中题支关系表实体类
 *
 * @author lxp
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ParentPaperQuestion对象", description = "试卷和题干及选中题支关系表")
@TableName("parent_paper_question")
public class ParentPaperQuestion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;
    /**
     * 试卷 ID
     */
    @ApiModelProperty(value = "试卷 ID")
    private Long paperId;
    /**
     * 题干 ID
     */
    @ApiModelProperty(value = "题干 ID")
    private Long questionId;
    /**
     * 父题干 ID
     */
    @ApiModelProperty(value = "父题干 ID")
    private Long pQuestionId;
    /**
     * 题型
     */
    @ApiModelProperty(value = "题型")
    private int questionType;
    /**
     * 答案题支 ID(多选数组)
     */
    @ApiModelProperty(value = "答案题支 ID(多选数组)")
    private String optionId;
    /**
     * 其他内容
     */
    @ApiModelProperty(value = "其他内容")
    private String otherContent;
}

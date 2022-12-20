package org.mentpeak.parent.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hzl
 * @data 2022年06月23日17:10
 */
@Data
public class QResult {

    /**
     * 题干ID
     */
    @ApiModelProperty(value = "题干ID")
    private String qId;
    /**
     * 题序
     */
    @ApiModelProperty(value = "题序")
    private int sort;
    /**
     * 题型
     */
    @ApiModelProperty(value = "题型")
    private int qType;
    /**
     * 单选
     */
    @ApiModelProperty(value = "单选")
    private SingleChoice singleChoice;
    /**
     * 多选
     */
    @ApiModelProperty(value = "多选")
    private List<SingleChoice> multipleChoice;
    /**
     * 父子题
     */
    @ApiModelProperty(value = "父子题|矩阵题")
    private List<Answer> answers;
    /**
     * 填空题
     */
    @ApiModelProperty(value = "填空题")
    private String inputContent;
}

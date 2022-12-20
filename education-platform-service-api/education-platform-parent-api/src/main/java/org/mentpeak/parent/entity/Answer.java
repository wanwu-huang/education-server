package org.mentpeak.parent.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hzl
 * @data 2022年06月24日10:29
 */
@Data
public class Answer {

    /**
     * 题干ID
     */
    @ApiModelProperty(value = "题干ID")
    private String qId;
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
}

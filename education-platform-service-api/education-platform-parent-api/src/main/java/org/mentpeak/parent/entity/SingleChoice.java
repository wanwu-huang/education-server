package org.mentpeak.parent.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @data 2022年06月24日9:45
 * 单选题
 */
@Data
public class SingleChoice {

    @ApiModelProperty(value = "题支id")
    private String oId;

    @ApiModelProperty(value = "题支类型")
    private int oType;

    @ApiModelProperty(value = "填空输入内容")
    private ExtResult extResult;
}

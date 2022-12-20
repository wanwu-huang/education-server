package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月09日18:21
 * 测评概况
 */
@Data
public class DimensionVO implements Serializable {
    @ApiModelProperty(value = "名称")
    private String dimensionName;
    @ApiModelProperty(value = "结果")
    private String result;
    @ApiModelProperty(value = "分数")
    private String score;
    @ApiModelProperty(value = "颜色")
    private String color;
}

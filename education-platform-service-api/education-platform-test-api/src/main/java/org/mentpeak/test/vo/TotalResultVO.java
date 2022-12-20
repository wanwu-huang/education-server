package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月16日19:09
 */
@Data
public class TotalResultVO implements Serializable {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "分数")
    private String score;

    @ApiModelProperty(value = "字体颜色")
    private String fontColor;
}

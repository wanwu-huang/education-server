package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hzl
 * @data 2022年08月09日18:21
 * 圆柱数据格式
 */
@Data
public class TargetVO implements Serializable {
    @ApiModelProperty(value = "名称")
    private String title;
    @ApiModelProperty(value = "分数")
    private String score;
    @ApiModelProperty(value = "颜色")
    private String fontColor;
}

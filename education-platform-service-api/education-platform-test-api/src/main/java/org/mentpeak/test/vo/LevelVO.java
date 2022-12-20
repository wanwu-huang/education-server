package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月09日17:43
 * 等级key-value
 */
@Data
public class LevelVO implements Serializable {

    @ApiModelProperty(value = "等级名称")
    private String key;
    @ApiModelProperty(value = "等级value")
    private String value;
}

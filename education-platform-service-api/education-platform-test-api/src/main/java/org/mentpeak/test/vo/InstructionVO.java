package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月15日16:16
 */
@Data
public class InstructionVO implements Serializable {

    @ApiModelProperty(value = "模块id")
    private Long moduleId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "名字")
    private String name;
}

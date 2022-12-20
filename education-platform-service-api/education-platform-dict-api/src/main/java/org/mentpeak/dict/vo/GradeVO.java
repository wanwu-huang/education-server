package org.mentpeak.dict.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @data 2022年06月27日15:33
 * 班级、年级
 */
@Data
public class GradeVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name;
}

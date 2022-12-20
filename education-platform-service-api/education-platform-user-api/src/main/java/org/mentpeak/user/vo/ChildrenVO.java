package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月21日10:37
 */
@Data
public class ChildrenVO implements Serializable {

    @ApiModelProperty(value = "任务id")
    private Long id;
    @ApiModelProperty(value = "任务名称")
    private String name;
    @ApiModelProperty(value = "是否选中 0否 1是")
    private int isSelected;
}

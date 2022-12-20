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
public class FollowLevelVO implements Serializable {

    @ApiModelProperty(value = "关注级别")
    private String FollowName;
    @ApiModelProperty(value = "人数")
    private Integer people = 0;
    @ApiModelProperty(value = "班级占比")
    private String classRatio = "0";
}

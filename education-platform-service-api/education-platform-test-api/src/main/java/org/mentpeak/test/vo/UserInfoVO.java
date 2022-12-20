package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月09日17:04
 */
@Data
public class UserInfoVO implements Serializable {

    @ApiModelProperty(value = "任务id")
    private Long taskId;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
}

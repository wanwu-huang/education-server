package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月09日17:04
 */
@Data
public class FollowUserVO implements Serializable {

    @ApiModelProperty(value = "一级用户id")
    private Long userIdOne;
    @ApiModelProperty(value = "一级用户姓名")
    private String userNameOne;

    @ApiModelProperty(value = "二级用户id")
    private Long userIdTwo;
    @ApiModelProperty(value = "二级用户姓名")
    private String userNameTwo;

    @ApiModelProperty(value = "三级用户id")
    private Long userIdThree;
    @ApiModelProperty(value = "三级用户姓名")
    private String userNameThree;
}

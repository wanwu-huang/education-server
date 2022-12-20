package org.mentpeak.test.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mentpeak.test.vo.UserInfoVO;

/**
 * @author hzl
 * @data 2022年08月09日17:18
 * 作答有效性
 */
@Data
public class FollowWarn extends UserInfoVO {

    @ApiModelProperty(value = "关注等级")
    private Integer followLevel;

    @ApiModelProperty(value = "预警等级")
    private Integer isWarn;

    @ApiModelProperty(value = "家长他评")
    private Integer parentComments;

    @ApiModelProperty(value = "教师他评")
    private Integer teacherComments;

    @ApiModelProperty(value = "学生自评")
    private Integer studentComments;
}

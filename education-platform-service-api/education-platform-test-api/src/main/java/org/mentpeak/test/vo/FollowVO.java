package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月19日16:29
 * 关注列表
 */
@Data
public class FollowVO implements Serializable {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty("性别 0 男 1 女")
    private Integer sex;

    @ApiModelProperty("身份证号")
    private String account;

    @ApiModelProperty("一级部门")
    private String grade;

    @ApiModelProperty("二级部门")
    private String className;

    @ApiModelProperty(value = "家长他评")
    private String parentComments;

    @ApiModelProperty(value = "教师他评")
    private String teacherComments;

    @ApiModelProperty(value = "学生自评")
    private String studentComments;
}

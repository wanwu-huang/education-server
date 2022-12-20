package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年09月05日17:55
 */
@Data
public class BindTeacherVO implements Serializable {

    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "身份证号码")
    private String idCard;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "角色")
    private String roleName;
    @ApiModelProperty(value = "绑定日期")
    private String createTime;
    @ApiModelProperty(value = "年级ID")
    private Long gradeId;
    @ApiModelProperty(value = "班级ID")
    private Long classId;
    @ApiModelProperty(value = "任务ID")
    private Long taskId;
}

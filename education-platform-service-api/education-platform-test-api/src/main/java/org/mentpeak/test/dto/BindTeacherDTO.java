package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年09月05日15:12
 */
@Data
public class BindTeacherDTO implements Serializable {

    @ApiModelProperty(value = "任务id", required = true)
    private Long taskId;

    @ApiModelProperty(value = "年级id", required = true)
    private Long gradeId;

    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;

    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    @ApiModelProperty(value = "身份证号码", required = true)
    private String idCard;

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "角色", required = true)
    private String roleName;
}

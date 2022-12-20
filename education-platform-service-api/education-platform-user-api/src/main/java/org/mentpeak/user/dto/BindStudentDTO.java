package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author hzl
 * @data 2022年06月28日10:44
 */
@Data
public class BindStudentDTO {

    @NotNull(message = "学校名称不能为空")
    @ApiModelProperty(value = "学校名称", required = true)
    private String schoolName;

    @NotNull(message = "年级id不能为空")
    @ApiModelProperty(value = "年级id", required = true)
    private Long GradeId;

    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long ClassId;

    @NotNull(message = "学生姓名不能为空")
    @ApiModelProperty(value = "学生姓名", required = true)
    private String studentName;

    @NotNull(message = "学生身份证号|学籍号不能为空")
    @ApiModelProperty(value = "学生身份证号|学籍号", required = true)
    private String studentNumber;

    @NotNull(message = "家长姓名不能为空")
    @ApiModelProperty(value = "家长姓名", required = true)
    private String parentsName;
}

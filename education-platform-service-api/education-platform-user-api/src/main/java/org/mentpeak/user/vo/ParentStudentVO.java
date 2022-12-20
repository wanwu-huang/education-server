package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hzl
 * @data 2022年06月30日15:29
 * 绑定学生
 */
@Data
public class ParentStudentVO {

    @ApiModelProperty(value = "学生id")
    private Long id;

    @ApiModelProperty(value = "学生姓名")
    private String name;

    @ApiModelProperty(value = "学校名称")
    private String schoolName;
}

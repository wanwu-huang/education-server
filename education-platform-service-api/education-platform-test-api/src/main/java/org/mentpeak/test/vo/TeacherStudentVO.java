package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 对应数据
 *
 * @author demain_lee
 * @since 2022-08-16
 */
@Data
@Builder
public class TeacherStudentVO {

    @ApiModelProperty(value = "编号")
    private Long userId;

    @ApiModelProperty(value = "姓名")
    private String realName;
}

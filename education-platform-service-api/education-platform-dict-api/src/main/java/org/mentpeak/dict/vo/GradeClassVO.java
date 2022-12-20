package org.mentpeak.dict.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 年级、班级信息
 */
@Data
public class GradeClassVO {

    @ApiModelProperty("年级ID")
    private Long gradeId;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("年级名称")
    private String gradeName;

    @ApiModelProperty("班级名称")
    private String className;
}

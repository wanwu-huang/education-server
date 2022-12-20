package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 教师评定
 */
@Data
@ApiModel(value = "ReportUserDTO对象", description = "个体报告")
public class TeacherPaperDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "一级部门ID")
	private Long gradeId ;

	@ApiModelProperty(value = "二级部门")
	private Long classId;

	@ApiModelProperty(value = "状态 1 已提交 2 暂时保存 3 未评定")
	private Integer status;

}

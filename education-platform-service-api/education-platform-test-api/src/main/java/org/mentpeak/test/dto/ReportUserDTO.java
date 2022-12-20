package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 个体报告
 */
@Data
@ApiModel(value = "ReportUserDTO对象", description = "个体报告")
public class ReportUserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "一级部门ID")
	private Long gradeId ;

	@ApiModelProperty(value = "二级部门")
	private Long classId;

	@ApiModelProperty(value = "姓名")
	private String realName;

	@ApiModelProperty(value = "任务ID",required = true)
	private Long taskId ;
}

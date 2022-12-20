package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 年级用户信息
 */
@Data
@ApiModel(value = "GradeDetailUserVO对象", description = "年级用户信息")
public class GradeDetailUserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "一级部门ID", required = true)
	@NotNull(message = "一级部门ID不能为空")
	private Long gradeId ;

	@ApiModelProperty(value = "姓名", required = false)
	private String realName;

	@ApiModelProperty(value = "学籍号", required = false)
	private String account;

	@ApiModelProperty(value = "二级部门", required = false)
	private Long classId;

	@ApiModelProperty(value = "性别", required = false)
	private Integer sex;
}

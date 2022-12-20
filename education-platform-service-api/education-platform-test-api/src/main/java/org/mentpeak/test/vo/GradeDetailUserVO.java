package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 年级用户信息
 */
@Data
@ApiModel(value = "GradeDetailUserVO对象", description = "年级用户信息")
public class GradeDetailUserVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户ID")
	private String id;

	@ApiModelProperty(value = "姓名")
	private String realName;

	@ApiModelProperty(value = "学籍号")
	private String account;

	@ApiModelProperty(value = "性别")
	private Integer sex;

	@ApiModelProperty(value = "性别名字")
	private String sexName;

	@ApiModelProperty(value = "二级部门")
	private String className;

	@ApiModelProperty(value = "导入时间")
	private String createTime;

}

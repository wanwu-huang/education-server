package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 年级信息
 */
@Data
@ApiModel(value = "GradeUserVO对象", description = "年级信息")
public class GradeUserVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "年级ID")
	private Long grade;

	@ApiModelProperty(value = "年级名字")
	private String gradeName;

	@ApiModelProperty(value = "人数")
	private Integer count;
}

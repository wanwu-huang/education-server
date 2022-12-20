package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 测评任务表数据传输对象实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
public class TestTaskDTO {

	/**
	 * 测评途径ID
	 */
	@ApiModelProperty(value = "测评途径ID")
	@NotNull(message = "测评途径ID不能为空")
	private Long approachId;

	/**
	 * 问卷ID
	 */
	@ApiModelProperty(value = "问卷ID")
	@NotEmpty(message = "问卷ID不能为空")
	private Long[] questionnaireId;

	/**
	 * 任务名称
	 */
	@ApiModelProperty(value = "任务名称")
	@NotBlank(message = "任务名称不能为空")
	private String taskName;

	/**
	 * 测评部门 对应 年级
	 */
	@ApiModelProperty(value = "测评部门")
	@NotEmpty(message = "测评部门不能为空")
	private Long[] gradeId;

	/**
	 * 任务开始时间
	 */
	@ApiModelProperty(value = "任务开始时间")
	@NotBlank(message = "任务开始时间不能为空")
	private String beginTime;

	/**
	 * 任务结束时间
	 */
	@ApiModelProperty(value = "任务结束时间")
	@NotBlank(message = "任务结束时间不能为空")
	private String endTime;

	/**
	 * 报告属性 【字典】
	 */
	@ApiModelProperty(value = "报告属性 【字典】")
	@NotNull(message = "报告属性不能为空")
	private Integer reportIsVisible;

}

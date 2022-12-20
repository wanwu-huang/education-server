package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 任务管理详情搜索实体类
 */
@Data
public class TaskDetailSearchDTO {

	/**
	 * 任务ID
	 */
	@ApiModelProperty(value = "任务ID",required = true)
	@NotNull(message = "任务ID不能为空")
	private Long testTaskId;

	/**
	 * 姓名
	 */
	@ApiModelProperty(value = "姓名")
	private String name;

	/**
	 * 学籍号
	 */
	@ApiModelProperty(value = "学籍号")
	private String account;

	/**
	 * 性别
	 */
	@ApiModelProperty(value = "性别")
	private Integer sex;

	/**
	 * 测评时间
	 */
	@ApiModelProperty(value = "测评开始时间")
	private String beginTime;

	/**
	 * 测评时间
	 */
	@ApiModelProperty(value = "测评结束时间")
	private String endTime;

	/**
	 * 一级部门
	 */
	@ApiModelProperty(value = "一级部门")
	private Long testDepartmentId;

	/**
	 * 二级部门
	 */
	@ApiModelProperty(value = "二级部门")
	private Long classId;

	/**
	 * 完成情况
	 */
	@ApiModelProperty(value = "完成情况 0 未完成 1已完成")
	private Integer completionStatus;

}

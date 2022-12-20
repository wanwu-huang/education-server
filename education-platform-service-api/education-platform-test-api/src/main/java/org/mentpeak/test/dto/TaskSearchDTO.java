package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 任务管理搜索实体类
 */
@Data
public class TaskSearchDTO {

	/**
	 * 任务名称
	 */
	@ApiModelProperty(value = "任务名称")
	private String taskName;

	/**
	 * 任务创建时间
	 */
	@ApiModelProperty(value = "任务创建时间")
	private String taskCreateTime;

	/**
	 * 任务开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	private String beginTime;

	/**
	 * 任务结束时间
	 */
	@ApiModelProperty(value = "结束时间")
	private String endTime;

	/**
	 * 任务状态
	 */
	@ApiModelProperty(value = "任务状态")
	private Integer taskStatus;

	/**
	 * 接口类型
	 */
	@ApiModelProperty(value = "接口类型 0 任务管理  1 报告管理")
	private Integer interfaceType;


}

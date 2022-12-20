package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 任务信息修改实体类
 */
@Data
public class TaskUpdateDTO {

	/**
	 * 任务ID
	 */
	@ApiModelProperty(value = "任务ID",required = true)
	@NotNull(message = "任务ID不能为空")
	private Long testTaskId;

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

}

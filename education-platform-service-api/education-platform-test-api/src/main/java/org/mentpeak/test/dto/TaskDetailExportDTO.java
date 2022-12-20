package org.mentpeak.test.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 任务管理详情搜索实体类
 */
@Data
public class TaskDetailExportDTO {

	/**
	 * 任务ID
	 */
	@ApiModelProperty(value = "任务ID",required = true)
	@NotNull(message = "任务ID不能为空")
	private Long testTaskId;

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID",required = true)
	@NotNull(message = "用户ID不能为空")
	private Long userId;

}

package org.mentpeak.test.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.entity.TestTask;

/**
 * 测评任务表视图实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TestTaskVO对象", description = "测评任务表")
public class TestTaskVO extends TestTask {
	private static final long serialVersionUID = 1L;


	/**
	 * 二维码 url
	 */
	@ApiModelProperty(value = "二维码 url")
	private String qrCode;

	/**
	 * 类型
	 */
	@ApiModelProperty(value = "类型 1 二维码扫描 2 测评链接 3 桌面程序")
	private Long type;
}

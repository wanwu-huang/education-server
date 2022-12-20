package org.mentpeak.test.base;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lxp
 * @date 2022/06/16 17:15
 * 题干基类，不包含选项，兼容父子题型
 **/
@Data
@ApiModel(value = "BaseQuestion基类", description = "BaseQuestion基类")
public class BaseQuestion {
	private static final long serialVersionUID = 1L;
	/**
	 * 题干ID
	 */
	@ApiModelProperty(value = "题干ID")
	private String qId;
	/**
	 * 题干标题
	 */
	@ApiModelProperty(value = "题干标题")
	private String qTitle;
	/**
	 * 题序
	 */
	@ApiModelProperty(value = "题序")
	private int sort;
	/**
	 * 选项ID
	 */
	@ApiModelProperty(value = "题型")
	private int qType;
}

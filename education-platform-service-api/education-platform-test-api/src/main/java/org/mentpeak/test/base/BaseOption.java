package org.mentpeak.test.base;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author lxp
 * @date 2022/06/16 17:15
 * 选项基类
 **/
@Data
@ApiModel(value = "Option基类", description = "Option基类")
public class BaseOption implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 选项ID
	 */
	@ApiModelProperty(value = "选项ID")
	private String oId;
	/**
	 * 选项标题
	 */
	@ApiModelProperty(value = "选项标题")
	private String oTitle;
	/**
	 * 选项类型
	 */
	@ApiModelProperty(value = "选项类型")
	private int oType;
}

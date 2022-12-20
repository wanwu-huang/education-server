package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年07月20日10:07
 */
@Data
public class Meta2VO implements Serializable {

    @ApiModelProperty("名称")
    private String title;

	@ApiModelProperty("一级二级标识")
	private String type;
}

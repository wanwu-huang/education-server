package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月02日18:34
 */
@Data
public class Children2VO implements Serializable {

    @ApiModelProperty("菜单id")
    private Long id;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("名称")
    private String title;

    @ApiModelProperty("一级二级标识")
    private String type;
}

package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hzl
 * @data 2022年08月02日18:34
 */
@Data
public class MenuChildrenVO implements Serializable {

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("名称")
    private Meta2VO metaVO;
}

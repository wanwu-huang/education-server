package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年07月20日10:07
 */
@Data
public class MenuListVO implements Serializable {

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("名称")
    private MetaVO metaVO;

    @ApiModelProperty("子菜单")
    private List<MenuChildrenVO> children;
}

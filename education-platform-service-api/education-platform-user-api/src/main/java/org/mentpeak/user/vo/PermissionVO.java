package org.mentpeak.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年07月21日10:31
 */
@Data
public class PermissionVO implements Serializable {

    @ApiModelProperty(value = "菜单id")
    private Long id;
    @ApiModelProperty(value = "菜单名称")
    private String name;
    @ApiModelProperty(value = "是否选中 0否 1是")
    private int isSelected = 0;
    @ApiModelProperty("子菜单")
    private List<ChildrenVO> children;
}

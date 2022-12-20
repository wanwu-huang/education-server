package org.mentpeak.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzl
 * @data 2022年07月21日15:08
 */
@Data
public class PermissionDTO implements Serializable {

    @ApiModelProperty("菜单id")
    private Integer id;
    @ApiModelProperty("子菜单id")
    private List<Long> children;
}

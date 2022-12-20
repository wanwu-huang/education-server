package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 平台-菜单-角色关系表实体类
 *
 * @author lxp
 * @since 2022-07-21
 */
@Data
@TableName("platform_role_menu")
@ApiModel(value = "RoleMenu对象", description = "平台-菜单-角色关系表")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 菜单id
     */
    @ApiModelProperty(value = "菜单id")
      private Long menuId;
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
      private Long roleId;


}

package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.core.mybatisplus.base.BaseEntity;

import java.io.Serializable;

/**
 * 菜单数据关联表实体类
 *
 * @author lxp
 * @since 2022-07-12
 */
@Data
@ApiModel(value = "MenuData对象", description = "菜单数据关联表")
public class MenuData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    /**
     * 菜单ID
     */
    @ApiModelProperty(value = "菜单ID")
    private Long menuId;
    /**
     * 数据ID 【任务ID】
     */
    @ApiModelProperty(value = "数据ID 【任务ID】")
    private Long dataId;
    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色ID")
    private Long roleId;
    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除")
    private int isDeleted;


}

package org.mentpeak.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 平台-菜单路由表实体类
 *
 * @author lxp
 * @since 2022-07-21
 */
@Data
@TableName("platform_menu")
@ApiModel(value = "Menu对象", description = "平台-菜单路由表")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 父级菜单
     */
    @ApiModelProperty(value = "父级菜单")
      private Long parentId;
    /**
     * 菜单编号
     */
    @ApiModelProperty(value = "菜单编号")
      private String code;
    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
      private String name;
    /**
     * 菜单别名
     */
    @ApiModelProperty(value = "菜单别名")
      private String alias;
    /**
     * 请求地址
     */
    @ApiModelProperty(value = "请求地址")
      private String path;
    /**
     * 菜单资源
     */
    @ApiModelProperty(value = "菜单资源")
      private String source;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
      private Integer sort;
    /**
     * 菜单类型（1：菜单，2：按钮）
     */
    @ApiModelProperty(value = "菜单类型（1：菜单，2：按钮）")
      private Integer category;
    /**
     * 操作按钮类型（1：新增按钮，2：修改/查看按钮，3：删除按钮）
     */
    @ApiModelProperty(value = "操作按钮类型（1：新增按钮，2：修改/查看按钮，3：删除按钮）")
      private Integer action;
    /**
     * 是否打开新页面
     */
    @ApiModelProperty(value = "是否打开新页面")
      private Integer isOpen;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
      private String remark;
    /**
     * 是否已删除
     */
    @ApiModelProperty(value = "是否已删除")
      private Integer isDeleted;


}
